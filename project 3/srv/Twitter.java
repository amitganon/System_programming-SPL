package bgu.spl.net.srv;

import bgu.spl.net.srv.Objects.*;
import bgu.spl.net.srv.Objects.RegisterCommand;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Twitter {
    private ConcurrentHashMap<String, User> users;//registered users
    private ConcurrentHashMap<String, List<String>> followers;//all followers of a user
    private ConcurrentHashMap<String,Boolean>loggedIn;// if the user is loggedIn
    private ConcurrentHashMap<String,List<Message>> privateMessages;
    private ConcurrentHashMap<String,List<Message>> postMessages;
    private ConcurrentHashMap<String,List<String>> blockedUsers;
    private List<String>filteredWords;
    private ConcurrentHashMap<Integer, User> userId; // all users by id

    public Twitter() {
        followers = new ConcurrentHashMap<>();
        users = new ConcurrentHashMap<>();
        privateMessages=new ConcurrentHashMap<>();
        postMessages = new ConcurrentHashMap<>();
        blockedUsers= new ConcurrentHashMap<>();
        loggedIn = new ConcurrentHashMap<>();
        filteredWords = Stream.of("grade_less_than_100","corona","alaadin","computer_science").collect(Collectors.toList());
        userId = new ConcurrentHashMap<>();
    }

    public Vector<ReturnCommand> Register(RegisterCommand cmd){
        Vector<ReturnCommand> result = new Vector<>();
        if(users.containsKey(cmd.getName())){ //already registered
            ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
            errcmd.setMsgOpCode(1);
            result.add(errcmd);
        }
        else{
            User user = new User(cmd.getSenderId(), cmd.getName(), cmd.getPassword(), cmd.getBirthday());
            users.put(user.getName(), user);
            followers.put(user.getName(),new LinkedList<>());
            loggedIn.put(user.getName(),false);
            blockedUsers.put(user.getName(),new LinkedList<>());

            AckCommand ackcmd = (AckCommand) CommandFactory.makeReturnCommand(10);
            ackcmd.setMsgOpCode(1);
            result.add(ackcmd);
        }
        return result;
    }

    public Vector<ReturnCommand> Login(LoginCommand command){
        Vector<ReturnCommand> result = new Vector<>();
        if(users.containsKey(command.getSenderName()) && !checkLoggedIn(command.getSenderName()) && command.getCaptcha() == 1) {
            synchronized (users.get(command.getSenderName())) {
                if (users.get(command.getSenderName()).getPassword().equals(command.getPassword())) {
                    if (!checkLoggedIn(command.getSenderName())) {
                        loggedIn.put(command.getSenderName(), true);
                        AckCommand ack = new AckCommand(10);
                        ack.setMsgOpCode(command.getOpCode());
                        result.add(ack);
                        users.get(command.getSenderName()).setID(command.getSenderId());
                        userId.put(command.getSenderId(),users.get(command.getSenderName()));
                        User user = users.get(command.getSenderName());
                        for (Message msg : user.getUnreadMsgAndReset()) {
                            NotificationCommand notificationCmd = new NotificationCommand(9);
                            if(msg.getType() == 0)
                                notificationCmd.setContent(msg.getContent()+" "+ msg.getSendingDate());
                            else
                                notificationCmd.setContent(msg.getContent());
                            notificationCmd.setPostingUserName(msg.getSenderName());
                            notificationCmd.setType(msg.getType());
                            result.add(notificationCmd);
                        }
                        return result;
                    }
                }
            }
        }
        ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
        errcmd.setMsgOpCode(2);
        result.add(errcmd);
        return result;
    }

    public Vector<ReturnCommand> Logout(LogoutCommand cmd){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(cmd.getSenderId()))
            cmd.setSenderName(userId.get(cmd.getSenderId()).getName());
        synchronized (users.get(cmd.getSenderName())) {
            if (checkRegister(cmd.getSenderId()) && checkLoggedIn(cmd.getSenderName())) {
                loggedIn.put(cmd.getSenderName(), false);
                userId.remove(cmd.getSenderId());
                AckCommand ackcmd = (AckCommand) CommandFactory.makeReturnCommand(10);
                ackcmd.setMsgOpCode(3);
                result.add(ackcmd);
            }
            else {
                ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
                errcmd.setMsgOpCode(3);
                result.add(errcmd);
            }
        }
        return result;
    }

    public Vector<ReturnCommand> Follow(FollowCommand command){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(command.getSenderId()))
            command.setSenderName(userId.get(command.getSenderId()).getName());
        if(checkRegister(command.getSenderId()) && checkLoggedIn(command.getSenderName()) && !command.getFollowName().equals(command.getSenderName())){
            if (users.containsKey(command.getFollowName()) && !followers.get(command.getFollowName()).contains(command.getSenderName())) {
                if (!BlockedUser(command.getSenderName(),command.getFollowName())) {
                    followers.get(command.getFollowName()).add(command.getSenderName());
                    User user1 = users.get(command.getSenderName());
                    user1.addFollowing();
                    User user2 = users.get(command.getFollowName());
                    user2.addFollowers();
                    AckCommand ack = new AckCommand(10);
                    ack.setMsgOpCode(command.getOpCode());
                    ack.setOptionalData(command.getFollowName());
                    result.add(ack);
                    return result;
                }
            }
        }
        ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
        errcmd.setMsgOpCode(4);
        result.add(errcmd);
        return result;
    }

    public Vector<ReturnCommand> UnFollow(FollowCommand command){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(command.getSenderId()))
            command.setSenderName(userId.get(command.getSenderId()).getName());
        if(checkRegister(command.getSenderId()) && checkLoggedIn(command.getSenderName())) {
            if (followers.get(command.getFollowName()).contains(command.getSenderName())) {
                followers.get(command.getFollowName()).remove(command.getSenderName());
                User user = users.get(command.getSenderName());
                user.subFollowing();
                User user2 = users.get(command.getFollowName());
                user2.subFollowers();
                AckCommand ack = new AckCommand(10);
                ack.setMsgOpCode(command.getOpCode());
                ack.setOptionalData(command.getFollowName());
                result.add(ack);
                return result;
            }
        }
        ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
        errcmd.setMsgOpCode(4);
        result.add(errcmd);
        return result;
    }

    public Vector<ReturnCommand> Post(PostCommand cmd){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        cmd.setSendingDate(formatter.format(date));
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(cmd.getSenderId()))
            cmd.setSenderName(userId.get(cmd.getSenderId()).getName());
        if(checkRegister(cmd.getSenderId()) && loggedIn.containsKey(cmd.getSenderName())){
            String filteredContent=cmd.getContent();
            filteredContent=FilterString(filteredContent);
            Message m = new Message(1,filteredContent, cmd.getSenderName(), cmd.getSendingDate());
            if(postMessages.containsKey(cmd.getSendingDate()))
                postMessages.get(cmd.getSendingDate()).add(m);
            else{
                List<Message> temp = new LinkedList<>();
                temp.add(m);
                postMessages.put(cmd.getSendingDate(),temp);
            }
            AckCommand ack = new AckCommand(10);
            ack.setMsgOpCode(cmd.getOpCode());
            result.add(ack);

            User user = users.get(cmd.getSenderName());
            user.addPostedPost();
            Vector<String> usersToNotify = cmd.getMentionedUsers();
            for(String sUser : followers.get(cmd.getSenderName())){
                if(!usersToNotify.contains(sUser))
                    usersToNotify.add(sUser);
            }
            for(String sUser : usersToNotify){
                if(users.containsKey(sUser) && !BlockedUser(cmd.getSenderName(), sUser)) {
                    NotificationCommand notificationCommand = (NotificationCommand) CommandFactory.makeReturnCommand(9);
                    notificationCommand.setType(1);
                    notificationCommand.setPostingUserName(cmd.getSenderName());
                    notificationCommand.setContent(filteredContent);
                    notificationCommand.setDestUserID(users.get(sUser).getID());
                    synchronized (users.get(cmd.getSenderName())) {
                        if (!checkLoggedIn(sUser)) {
                            users.get(sUser).addUnreadMsg(m);
                        }
                        else{
                            result.add(notificationCommand);
                        }
                    }
                }
            }
            return result;
        }
        else{
            ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
            errcmd.setMsgOpCode(5);
            result.add(errcmd);
        }
        return result;
    }

    public Vector<ReturnCommand> PrivateMessage(PrivateMessageCommand command){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(command.getSenderId()))
            command.setSenderName(userId.get(command.getSenderId()).getName());
        if(checkRegister(command.getSenderId()) && checkLoggedIn(command.getSenderName()) && !command.getSenderName().equals(command.getReceiveName())){
            if(users.containsKey(command.getReceiveName())){
                if(followers.get(command.getReceiveName()).contains(command.getSenderName())
                        && !BlockedUser(command.getReceiveName(),command.getSenderName())){
                    String filteredContent=command.getContent();
                    filteredContent=FilterString(filteredContent);
                    Message m = new Message(0,filteredContent, command.getSenderName(), command.getSendingDate());
                    if(privateMessages.containsKey(command.getSendingDate().substring(0,10)))
                        privateMessages.get(command.getSendingDate().substring(0,10)).add(m);
                    else{
                        List <Message> temp = new LinkedList<>();
                        temp.add(m);
                        privateMessages.put(command.getSendingDate().substring(0,10),temp);
                    }
                    AckCommand ack = new AckCommand(10);
                    ack.setMsgOpCode(command.getOpCode());
                    result.add(ack);
                    NotificationCommand notCommand = new NotificationCommand(command.getOpCode());
                    notCommand.setType(0);
                    notCommand.setPostingUserName(command.getSenderName());
                    notCommand.setContent(filteredContent+" "+command.getSendingDate());
                    notCommand.setDestUserID(users.get(command.getReceiveName()).getID());
                    synchronized (users.get(command.getSenderName())) {
                        if (!checkLoggedIn(command.getReceiveName()))
                            users.get(command.getReceiveName()).addUnreadMsg(m);
                        else
                            result.add(notCommand);
                    }
                    return result;
                }
            }
        }
        ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
        errcmd.setMsgOpCode(6);
        result.add(errcmd);
        return result;
    }

    public Vector<ReturnCommand> LogStat(LogStatCommand cmd){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(cmd.getSenderId()))
            cmd.setSenderName(userId.get(cmd.getSenderId()).getName());
        if(checkRegister(cmd.getSenderId()) && loggedIn.containsKey(cmd.getSenderName())){
            int ackSendCounter =0;
            for(User user : users.values()){
                if(user.getName() != userId.get(cmd.getSenderId()).getName() && loggedIn.get(user.getName())) {
                    if (!blockedUsers.get(user.getName()).contains(cmd.getSenderName())) {
                        AckCommand ackcmd = (AckCommand) CommandFactory.makeReturnCommand(10);
                        ackcmd.setMsgOpCode(7);
                        ackcmd.setOptionalData(user.getStats());
                        result.add(ackcmd);
                        ackSendCounter++;
                    }
                }
            }
            if(ackSendCounter==0) {
                AckCommand ackcmd = (AckCommand) CommandFactory.makeReturnCommand(10);
                ackcmd.setMsgOpCode(7);
                result.add(ackcmd);
            }
        }
        else{
            ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
            errcmd.setMsgOpCode(7);
            result.add(errcmd);
        }
        return result;
    }

    public Vector<ReturnCommand> Stats(StatsCommand command){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(command.getSenderId()))
            command.setSenderName(userId.get(command.getSenderId()).getName());
        if(checkRegister(command.getSenderId()) && checkLoggedIn(command.getSenderName())){
            for (String user:command.getUserNameList()) {
                if(!users.containsKey(user) || BlockedUser(command.getSenderName(),user)) {
                    ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
                    errcmd.setMsgOpCode(8);
                    result.add(errcmd);
                    return result;
                }
            }
            for (int i=0; i<command.getUserNameList().size();i++){
                AckCommand ack = new AckCommand(10);
                ack.setMsgOpCode(command.getOpCode());
                User user = users.get(command.getUserNameList().get(i));
                ack.setOptionalData(user.getStats());
                result.add(ack);
            }
        }
        else{
            ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
            errcmd.setMsgOpCode(8);
            result.add(errcmd);
        }
        return result;
    }

    public Vector<ReturnCommand> Block(BlockCommand command){
        Vector<ReturnCommand> result = new Vector<>();
        if(checkRegister(command.getSenderId()))
            command.setClientName(userId.get(command.getSenderId()).getName());
        if(checkRegister(command.getSenderId()) && users.containsKey(command.getBlockedName()) && checkLoggedIn(command.getClientName())){
            User user_1 = users.get(command.getClientName()); // client
            User user_2 = users.get(command.getBlockedName()); // blocked user
            if(!user_1.getName().equals(user_2.getName())) {
                if (followers.get(user_1.getName()).contains(user_2.getName())) {
                    followers.get(user_1.getName()).remove(user_2.getName());
                    user_1.subFollowers();
                    user_2.subFollowing();
                }
                if (followers.get(user_2.getName()).contains(user_1.getName())) {
                    followers.get(user_2.getName()).remove(user_1.getName());
                    user_2.subFollowers();
                    user_1.subFollowing();
                }
                if (!BlockedUser(command.getClientName(), command.getBlockedName())) {
                    if (!blockedUsers.containsKey(command.getClientName()))
                        blockedUsers.put(command.getClientName(), new LinkedList<>());
                    if (!blockedUsers.containsKey(command.getBlockedName()))
                        blockedUsers.put(command.getBlockedName(), new LinkedList<>());

                    blockedUsers.get(command.getClientName()).add(command.getBlockedName());
                    blockedUsers.get(command.getBlockedName()).add(command.getClientName());
                    AckCommand ack = new AckCommand(10);
                    ack.setMsgOpCode(command.getOpCode());
                    result.add(ack);
                    return result;
                }
            }
        }
        ErrorCommand errcmd = (ErrorCommand) CommandFactory.makeReturnCommand(11);
        errcmd.setMsgOpCode(12);
        result.add(errcmd);
        return result;
    }

    private boolean checkLoggedIn(String name){
        if(!loggedIn.get(name))
            return false;
        return true;
    }

    private boolean checkRegister(int id){
        if(!userId.containsKey(id))
            return false;
        return true;
    }

    private boolean BlockedUser(String client, String client2){
        if(blockedUsers.containsKey(client)) {
            for (String str : blockedUsers.get(client)) {
                if (client2.equals(str))
                    return true;
            }
        }
        return false;
    }

    private String FilterString(String unFilteredStr){
        String filteredString = unFilteredStr;
        for (String str:filteredWords) {
            str = "(?i)"+str.toLowerCase();
            filteredString =filteredString.replaceAll(str,"<filtered>");
        }
        return filteredString;
    }
}
