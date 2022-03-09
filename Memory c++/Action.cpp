
#include "Action.h"

#include "Studio.h"
extern Studio *backup;

BaseAction::BaseAction() : errorMsg(""), status(COMPLETED)
{
}

BaseAction::BaseAction(const BaseAction &other) : actionString(other.actionString), errorMsg(other.errorMsg), status(other.status)
{
}

ActionStatus BaseAction::getStatus() const
{
	return status;
}

void BaseAction::setActionString(std::string str)
{
	actionString = str;
}

void BaseAction::complete()
{
	status = COMPLETED;
}

void BaseAction::error(std::string errorMsg)
{
	status = ERROR;
	this->errorMsg = errorMsg;
}

std::string BaseAction::getErrorMsg() const
{
	return errorMsg;
}

BaseAction::~BaseAction()
{
}

OpenTrainer::OpenTrainer(int id, std::vector<Customer *> &customersList) : trainerId(id), customers(customersList)
{
}

void OpenTrainer::act(Studio &studio)
{
	Trainer *trainer = studio.getTrainer(trainerId);
	if (trainer == nullptr || trainer->isOpen())
	{
		error("Workout session does not exist or is already open.");
		cout << "Error: Trainer does not exist or is not open." << endl;
		vector<Customer *>::iterator it;
		for (it = customers.begin(); it != customers.end(); it++)
		{
			studio.setCustomerId(studio.getCustomerId() - 1);
			delete *it;
		}
		customers.clear();
	}
	else
	{
		trainer->openTrainer();
		std::vector<Customer *>::iterator it;
		for (it = customers.begin(); it < customers.end(); it++)
		{
			trainer->addCustomer(*it);
			if ((unsigned int)trainer->getCapacity() == trainer->getCustomers().size())
				break;
		}
		complete();
	}
	studio.addActionToLog(new OpenTrainer(*this));
}

std::string OpenTrainer::toString() const
{
	if (getStatus() == COMPLETED)
		return actionString + " Completed";
	else
		return actionString + " Error: " + getErrorMsg();
}
OpenTrainer *OpenTrainer::getInstance()
{
	return new OpenTrainer(*this);
}

Order::Order(int id) : trainerId(id)
{
}

void Order::act(Studio &studio)
{
	Trainer *trainer = studio.getTrainer(trainerId);
	if (trainer == nullptr || !trainer->isOpen())
	{
		error("Trainer does not exist or is not open");
		cout << "Error: Trainer does not exist or is not open" << endl;
	}
	else
	{
		std::vector<Customer *> customers = trainer->getCustomers();
		std::vector<Customer *>::iterator it;
		for (it = customers.begin(); it < customers.end(); it++)
		{
			trainer->order((*it)->getId(), (*it)->order(studio.getWorkoutOptions()), studio.getWorkoutOptions());
		}
		complete();
	}
	studio.addActionToLog(new Order(*this));
}

std::string Order::toString() const
{
	if (getStatus() == COMPLETED)
		return actionString + " Completed";
	else
		return actionString + " Error: " + getErrorMsg();
}
Order *Order::getInstance()
{
	return new Order(*this);
}

MoveCustomer::MoveCustomer(int src, int dst, int customerId) : srcTrainer(src), dstTrainer(dst), id(customerId)
{
}

void MoveCustomer::act(Studio &studio)
{
	Trainer *srcTrainer = studio.getTrainer(this->srcTrainer);
	Trainer *dstTrainer = studio.getTrainer(this->dstTrainer);
	Customer *cus = srcTrainer->getCustomer(this->id);
	if (srcTrainer == nullptr || dstTrainer == nullptr || cus == nullptr || (unsigned int)dstTrainer->getCapacity() == dstTrainer->getCustomers().size() || !dstTrainer->isOpen() || !srcTrainer->isOpen())
	{
		error("Cannot move customer");
		cout << "Error: Cannot move customer" << endl;
	}
	else
	{
		vector<OrderPair>::iterator it;
		for (it = srcTrainer->getOrders().begin(); it != srcTrainer->getOrders().end(); it++)
		{
			if (it->first == cus->getId())
			{
				dstTrainer->setSalary(dstTrainer->getSalary() + it->second.getPrice());
				dstTrainer->addOrder(*it);
			}
		}

		srcTrainer->removeCustomer(this->id);
		dstTrainer->addCustomer(cus);

		if (srcTrainer->getCustomers().size() == 0)
			srcTrainer->closeTrainer();
		complete();
	}
	studio.addActionToLog(new MoveCustomer(*this));
}

std::string MoveCustomer::toString() const
{
	if (getStatus() == COMPLETED)
		return actionString + " Completed";
	else
		return actionString + " Error: " + getErrorMsg();
}
MoveCustomer *MoveCustomer::getInstance()
{
	return new MoveCustomer(*this);
}

Close::Close(int id) : trainerId(id)
{
}

void Close::act(Studio &studio)
{
	Trainer *trainer = studio.getTrainer(trainerId);
	if (trainer == nullptr || !trainer->isOpen())
	{
		error("Trainer does not exist or is not open");
		cout << "Error: Trainer does not exist or is not open" << endl;
		delete (trainer);
	}
	else
	{
		trainer->closeTrainer();
		complete();
	}
	studio.addActionToLog(new Close(*this));
}

std::string Close::toString() const
{
	if (getStatus() == COMPLETED)
		return actionString + " Completed";
	else
		return actionString + " Error: " + getErrorMsg();
}
Close *Close::getInstance()
{
	return new Close(*this);
}

CloseAll::CloseAll()
{
}

void CloseAll::act(Studio &studio)
{
	for (int i = 0; i < studio.getNumOfTrainers(); i++)
	{
		Trainer &trainer = *studio.getTrainer(i);
		if (trainer.isOpen())
			trainer.closeTrainer();
	}

	complete();
	studio.addActionToLog(new CloseAll(*this));
}

std::string CloseAll::toString() const
{
	return std::string();
}
CloseAll *CloseAll::getInstance()
{
	return new CloseAll(*this);
}

PrintWorkoutOptions::PrintWorkoutOptions()
{
}

void PrintWorkoutOptions::act(Studio &studio)
{
	vector<Workout> workout_options = studio.getWorkoutOptions();
	vector<Workout>::iterator it;
	for (it = workout_options.begin(); it < workout_options.end(); it++)
	{
		cout << it->getName() << ", " << it->EnumToString(it->getType()) << ", " << it->getPrice() << endl;
	}
	complete();
	studio.addActionToLog(new PrintWorkoutOptions(*this));
}

std::string PrintWorkoutOptions::toString() const
{
	return actionString + " Completed";
}
PrintWorkoutOptions *PrintWorkoutOptions::getInstance()
{
	return new PrintWorkoutOptions(*this);
}

PrintTrainerStatus::PrintTrainerStatus(int id) : trainerId(id)
{
}

void PrintTrainerStatus::act(Studio &studio)
{
	Trainer *trainer = studio.getTrainer(trainerId);
	if (trainer == nullptr || !trainer->isOpen())
		error("Trainer does not exist or is not open");
	cout << trainer->toString() << endl;
	complete();
	studio.addActionToLog(new PrintTrainerStatus(*this));
}

std::string PrintTrainerStatus::toString() const
{
	if (getStatus() == COMPLETED)
		return actionString + " Completed";
	else
		return actionString + " Error: " + getErrorMsg();
}
PrintTrainerStatus *PrintTrainerStatus::getInstance()
{
	return new PrintTrainerStatus(*this);
}

PrintActionsLog::PrintActionsLog()
{
}

void PrintActionsLog::act(Studio &studio)
{
	vector<BaseAction *>::const_iterator it;
	for (it = studio.getActionsLog().begin(); it != studio.getActionsLog().end(); it++)
	{
		cout << (*it)->toString() << endl;
	}
	complete();
	studio.addActionToLog(new PrintActionsLog(*this));
}

std::string PrintActionsLog::toString() const
{
	return actionString + " Completed";
}
PrintActionsLog *PrintActionsLog::getInstance()
{
	return new PrintActionsLog(*this);
}

BackupStudio::BackupStudio()
{
}

void BackupStudio::act(Studio &studio)
{
	if (backup != nullptr)
		delete backup;
	complete();
	studio.addActionToLog(new BackupStudio(*this));
	backup = new Studio(studio);
}

std::string BackupStudio::toString() const
{
	return actionString + " Completed";
}
BackupStudio *BackupStudio::getInstance()
{
	return new BackupStudio(*this);
}

RestoreStudio::RestoreStudio()
{
}

void RestoreStudio::act(Studio &studio)
{
	if (backup == nullptr)
	{ 
		error("No backup available");
		cout << "Error: No backup available" << endl;
	}
	else
	{
		studio = *backup;
		complete();
	}
	studio.addActionToLog(new RestoreStudio(*this));
}

std::string RestoreStudio::toString() const
{
	if (getStatus() == COMPLETED)
		return actionString + " Completed";
	else
		return actionString + " Error: " + getErrorMsg();
}
RestoreStudio *RestoreStudio::getInstance()
{
	return new RestoreStudio(*this);
}