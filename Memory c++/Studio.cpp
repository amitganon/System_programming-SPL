#include "Studio.h"
#include <fstream>
#include "Action.h"

using namespace std;

Studio::Studio() : open(false), trainers(), workout_options(), actionsLog()
{
    customerID=0;
}

Studio::Studio(const string& configFilePath)
{
    open=true;
    customerID=0;
    int trainerID = 0;
    int workoutID = 0;
    string line;
    int inputType = 1;
    bool inputNext = false;
    ifstream configFile(configFilePath);
    if (configFile.is_open())
    {
        while (getline(configFile, line))
        {
            if (line != "") {
                if (line.at(0) == '#') {
                    inputNext = true;
                }
                else if (inputNext) {
                    if (inputType == 1) {
                        //num of trainers
                        inputNext = false;
                        inputType++;
                    }
                    else if (inputType == 2) {
                        vector<string> listOfCapacities = split(line, ",");
                        vector<string>::iterator it;
                        for (it = listOfCapacities.begin(); it < listOfCapacities.end(); it++) {
                            Trainer* t = new Trainer(stoi(*it));
                            t->setId(trainerID);
                            trainerID++;
                            trainers.emplace_back(t);
                            //delete t;
                        }
                        inputNext = false;
                        inputType++;
                    }
                    else if (inputType == 3) {
                        vector<string> workoutSplit = split(line, ", ");
                        Workout w(workoutID, workoutSplit.at(0), stoi(workoutSplit.at(2)), stringToWorkoutType(workoutSplit.at(1)));
                        workout_options.push_back(w);
                        workoutID++;
                    }
                }
            }
        }
        configFile.close();
    }
}

Studio::Studio(const Studio& Studio) : open(Studio.open), customerID(Studio.customerID), workout_options(Studio.workout_options)
{
    
    vector<BaseAction*>::const_iterator it;
    for (it = Studio.getActionsLog().begin(); it != Studio.getActionsLog().end(); it++)
        actionsLog.push_back((*it)->getInstance());
    
    vector<Trainer*>::const_iterator it2;
    for (it2 = Studio.trainers.begin(); it2 != Studio.trainers.end(); it2++)
        trainers.push_back(new Trainer(**it2));
    
}

Studio::Studio(Studio&& Studio): open(Studio.open), customerID(Studio.customerID), workout_options(Studio.workout_options)
{
    vector<BaseAction*>::const_iterator it;
    for (it = Studio.getActionsLog().begin(); it != Studio.getActionsLog().end(); it++) {
        actionsLog.push_back(*it);
        Studio.actionsLog.at(it- Studio.getActionsLog().begin()) = nullptr;
    }
    vector<Trainer*>::const_iterator it2;
    for (it2 = Studio.trainers.begin(); it2 != Studio.trainers.end(); it2++){
        trainers.push_back(*it2);
        Studio.trainers.at(it2 - Studio.trainers.begin()) = nullptr;
    }
}





void Studio::start()
{
    cout << "Studio is now open!" << endl;
    string inputString;
    getline(cin, inputString);
    while (inputString != "closeall")
    {
        string command = inputString.substr(0, inputString.find_first_of(' '));
        vector<string> vecOfInput = split(inputString, " ");

        if (command == "open" && (int)vecOfInput.size() >= 3)
        {
            vector<string> vecOfStringCustomers(vecOfInput.size() - 2);
            for (unsigned int i = 2; i < vecOfInput.size();i++) {
                vecOfStringCustomers.at(i - 2) = vecOfInput.at(i);
            }
            vector<Customer*> vecOfCustomers;
            int counter=0;
            vector<string>::iterator it;
            string log_string = "open " + vecOfInput.at(1);
            for (it = vecOfStringCustomers.begin(); it < vecOfStringCustomers.end() && (getTrainer(stoi(vecOfInput.at(1))) != nullptr && counter < getTrainer(stoi(vecOfInput.at(1)))->getCapacity()); it++) {
                string name = split(*it, ",").at(0);
                string customerType = split(*it, ",").at(1);
                log_string.append(" " + name);
                if (customerType == "swt") {
                    vecOfCustomers.emplace_back(new SweatyCustomer (name, customerID));
                    log_string.append(",swt");
                }
                else if (customerType == "chp") {
                    vecOfCustomers.emplace_back(new CheapCustomer(name, customerID));
                    log_string.append(",chp");
                }
                else if (customerType == "mcl") {
                    vecOfCustomers.emplace_back(new HeavyMuscleCustomer(name, customerID));
                    log_string.append(",mcl");
                }
                else if (customerType == "fbd") {
                    vecOfCustomers.emplace_back(new FullBodyCustomer(name, customerID));
                    log_string.append(",fbd");
                }
                counter++;
                customerID++;

            }

            OpenTrainer openTrainerAction(stoi(vecOfInput.at(1)), vecOfCustomers);
            openTrainerAction.setActionString(log_string);
            openTrainerAction.act(*this);            
        }
        else if (command == "order")
        {
            Order orderAction(stoi(vecOfInput.at(1)));
            orderAction.setActionString(inputString);
            orderAction.act(*this);
        }
        else if (command == "close")
        {
            Close closeAction(stoi(vecOfInput.at(1)));
            closeAction.setActionString(inputString);
            closeAction.act(*this);
        }
        else if (command == "status")
        {
            PrintTrainerStatus trainerStatusAction(stoi(vecOfInput.at(1)));
            trainerStatusAction.setActionString(inputString);
            trainerStatusAction.act(*this);
            
        }
        else if (command == "move")
        {
            int src_trainer_id = stoi(vecOfInput.at(1));
            int dst_trainer_id = stoi(vecOfInput.at(2));
            int customer_id = stoi(vecOfInput.at(3));
            MoveCustomer moveCustomerAction(src_trainer_id, dst_trainer_id, customer_id);
            moveCustomerAction.setActionString(inputString);
            moveCustomerAction.act(*this);
        }
        else if (command == "workout_options")
        {
            PrintWorkoutOptions printWorkoutOptionsAction;
            printWorkoutOptionsAction.setActionString(inputString);
            printWorkoutOptionsAction.act(*this);
        }
        else if (command == "log") {
            PrintActionsLog printActionLog;
            printActionLog.setActionString(inputString);
            printActionLog.act(*this);
            
        }
        else if (command == "backup") {
            BackupStudio backupStudio;
            backupStudio.setActionString(inputString);
            backupStudio.act(*this);
        }
        else if (command == "restore") {
            RestoreStudio restoreStudio;
            restoreStudio.setActionString(inputString);
            restoreStudio.act(*this);
        }
        getline(cin, inputString);
    }

    CloseAll closeAllAction;
    closeAllAction.setActionString(inputString);
    closeAllAction.act(*this);
    open = false;
}

int Studio::getNumOfTrainers() const
{
    return (int)trainers.size();
}

Trainer* Studio::getTrainer(int tid)
{
    if(tid < (int)trainers.size() || tid < 0)
        return trainers.at(tid);
    return nullptr;
}

const std::vector<BaseAction*>& Studio::getActionsLog() const
{
    return actionsLog;
}

std::vector<Workout>& Studio::getWorkoutOptions()
{
    return workout_options;
}

WorkoutType Studio::stringToWorkoutType(string str)
{
    if (str == "Anaerobic") {
        return WorkoutType(0);
    }
    else if (str == "Mixed") {
        return WorkoutType(1);
    }
    else if (str == "Cardio") {
        return WorkoutType(2);
    }
    return WorkoutType(0);
}

vector<string> Studio::split(string s, string delimiter)
{
    size_t pos_start = 0, pos_end, delim_len = delimiter.length();
    string token;
    vector<string> res;

    while ((pos_end = s.find(delimiter, pos_start)) != string::npos)
    {
        token = s.substr(pos_start, pos_end - pos_start);
        pos_start = pos_end + delim_len;
        res.push_back(token);
    }

    res.push_back(s.substr(pos_start));
    return res;
}

void Studio::addActionToLog(BaseAction* action)
{
    actionsLog.push_back(action);
}




Studio::~Studio()
{
    vector<Trainer*>::iterator it;
    for (it = trainers.begin(); it != trainers.end(); it++) {
        delete (*it);
    }
    trainers.clear();

    vector<BaseAction*>::iterator it2;
    for (it2 = actionsLog.begin(); it2 != actionsLog.end(); it2++) {
        delete (*it2);
    }
    actionsLog.clear();

}

Studio& Studio::operator=(const Studio& other)
{
    if (this != &other) {

        customerID = other.customerID;
        open = other.open;
        vector<Trainer*>::iterator it4;
        for (it4 = trainers.begin(); it4 != trainers.end(); it4++) {
            delete *it4;
        }
        trainers.clear();

        vector<BaseAction*>::iterator it5;
        for (it5 = actionsLog.begin(); it5 != actionsLog.end(); it5++) {
            delete *it5;
        }
        actionsLog.clear();

        vector<Trainer*>::const_iterator it;
        for (it = other.trainers.begin(); it != other.trainers.end(); it++) {
            trainers.push_back(new Trainer (**it));
        }

        vector<BaseAction*>::const_iterator it2;
        for (it2 = other.actionsLog.begin(); it2 != other.actionsLog.end(); it2++) {
            actionsLog.push_back((*it2)->getInstance());
        }

        workout_options.clear();
        vector<Workout>::const_iterator it3;
        for (it3 = other.workout_options.begin(); it3 != other.workout_options.end(); it3++) {
            Workout w(*it3);
            workout_options.push_back(w);
        }
    }
    return *this;
}

Studio& Studio::operator=(Studio&& other)
{
    if (this != &other) {

        customerID = other.customerID;
        open = other.open;

        vector<Trainer*>::iterator it4;
        for (it4 = trainers.begin(); it4 != trainers.end(); it4++) {
            delete (*it4);
        }
        trainers.clear();

        vector<BaseAction*>::iterator it5;
        for (it5 = actionsLog.begin(); it5 != actionsLog.end(); it5++) {
            delete (*it5);
        }
        actionsLog.clear();

        vector<Trainer*>::const_iterator it;
        for (it = other.trainers.begin(); it != other.trainers.end(); it++) {
            trainers.push_back(*it);
            other.trainers.at(it - other.trainers.begin()) = nullptr;
        }
        vector<BaseAction*>::const_iterator it2;
        for (it2 = other.actionsLog.begin(); it2 != other.actionsLog.end(); it2++) {
            actionsLog.push_back(*it2);
            other.actionsLog.at(it2 - other.actionsLog.begin()) = nullptr;
        }

        vector<Workout>::const_iterator it3;
        for (it3 = other.workout_options.begin(); it3 != other.workout_options.end(); it3++) {
            Workout w(*it3);
            workout_options.push_back(w);
        }
    }

    return *this;
}

void Studio::setCustomerId(int id) {
    customerID=id;
}

int Studio::getCustomerId()const {
    return customerID;
}
