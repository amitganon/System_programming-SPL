#include "Customer.h"
#include <algorithm>

using namespace std;

Customer::Customer(std::string c_name, int c_id) : name(c_name), id(c_id)
{
}

std::string Customer::toString() const
{
    return to_string(id) + " " + name;
}

std::string Customer::getName() const
{
    return name;
}

int Customer::getId() const
{
    return id;
}

SweatyCustomer::SweatyCustomer(std::string name, int id) : Customer(name, id)
{
}

std::vector<int> SweatyCustomer::order(const std::vector<Workout> &workout_options)
{
    vector<int> SweatyCustomerOrder;
    vector<Workout>::const_iterator it;
    for (it = workout_options.begin(); it != workout_options.end(); it++)
    {
        if (it->getType() == WorkoutType(2))
            SweatyCustomerOrder.push_back(it->getId());
    }
    return SweatyCustomerOrder;
}

std::string SweatyCustomer::toString() const
{
    return Customer::toString();
}

SweatyCustomer *SweatyCustomer::getInstance()
{
    return new SweatyCustomer(*this);
}

CheapCustomer::CheapCustomer(std::string name, int id) : Customer(name, id)
{
}

std::vector<int> CheapCustomer::order(const std::vector<Workout> &workout_options)
{
    vector<int> CheapCustomerOrder;
    CheapCustomerOrder.push_back(findCheapestWorkout(workout_options).getId());
    return CheapCustomerOrder;
}

std::string CheapCustomer::toString() const
{
    return Customer::toString();
}

CheapCustomer *CheapCustomer::getInstance()
{
    return new CheapCustomer(*this);
}

HeavyMuscleCustomer::HeavyMuscleCustomer(std::string name, int id) : Customer(name, id)
{
}

std::vector<int> HeavyMuscleCustomer::order(const std::vector<Workout> &workout_options)
{
    vector<pair<int, int>> HeavyCustomerOrder;
    vector<Workout>::const_iterator it;
    for (it = workout_options.begin(); it != workout_options.end(); it++)
    {
        if (it->getType() == ANAEROBIC)
        {
            HeavyCustomerOrder.emplace_back(make_pair(it->getPrice(), it->getId()));
        }
    }

    sort(HeavyCustomerOrder.begin(), HeavyCustomerOrder.end(), greater<pair<int, int>>());
    int count = 0;
    int temp = 0;
    while (count < (int)HeavyCustomerOrder.size())
    {
        temp = HeavyCustomerOrder.at(count).second;
        if (count + 1 < (int)HeavyCustomerOrder.size() && HeavyCustomerOrder.at(count + 1).second < temp)
        {
            swap(HeavyCustomerOrder.at(count), HeavyCustomerOrder.at(count + 1));
        }
        count++;
    }
    vector<int> result;
    vector<pair<int, int>>::const_iterator it2;
    for (it2 = HeavyCustomerOrder.begin(); it2 != HeavyCustomerOrder.end(); it2++)
    {
        result.push_back(it2->second);
    }
    return result;
}

std::string HeavyMuscleCustomer::toString() const
{
    return Customer::toString();
}

HeavyMuscleCustomer *HeavyMuscleCustomer::getInstance()
{
    return new HeavyMuscleCustomer(*this);
}

FullBodyCustomer::FullBodyCustomer(std::string name, int id) : Customer(name, id)
{
}

std::vector<int> FullBodyCustomer::order(const std::vector<Workout> &workout_options)
{
    vector<Workout> CardioWorkouts;
    vector<Workout> MixedWorkouts;
    vector<Workout> AnaerobicWorkouts;
    vector<Workout>::const_iterator it;
    for (it = workout_options.begin(); it != workout_options.end(); it++)
    {
        if (it->getType() == ANAEROBIC)
            AnaerobicWorkouts.push_back(*it);
        else if (it->getType() == MIXED)
            MixedWorkouts.push_back(*it);
        else if (it->getType() == CARDIO)
            CardioWorkouts.push_back(*it);
    }

    vector<int> result;
    if (AnaerobicWorkouts.empty() || MixedWorkouts.empty() || CardioWorkouts.empty())
    {
        return result;
    }
    result.push_back(findCheapestWorkout(CardioWorkouts).getId());
    result.push_back(findExpensiveWorkout(MixedWorkouts).getId());
    result.push_back(findCheapestWorkout(AnaerobicWorkouts).getId());

    return result;
}

std::string FullBodyCustomer::toString() const
{
    return Customer::toString();
}
FullBodyCustomer *FullBodyCustomer::getInstance()
{
    return new FullBodyCustomer(*this);
}

Workout Customer::findCheapestWorkout(std::vector<Workout> workouts)
{
    vector<Workout>::const_iterator it;
    int min_price = workouts.at(0).getPrice();
    int min_id = workouts.at(0).getId();

    for (it = workouts.begin(); it != workouts.end(); it++)
    {
        if (it->getPrice() < min_price || (it->getPrice() == min_price && it->getId() < min_id))
        {
            min_price = it->getPrice();
            min_id = it->getId();
        }
    }

    for (it = workouts.begin(); it != workouts.end(); it++)
    {
        if (it->getId() == min_id)
        {
            return *it;
        }
    }
    return *it;
}

Workout Customer::findExpensiveWorkout(std::vector<Workout> workouts)
{
    vector<Workout>::const_iterator it;
    int max_price = workouts.at(0).getPrice();
    int max_id = workouts.at(0).getId();

    for (it = workouts.begin(); it != workouts.end(); it++)
    {
        if (it->getPrice() > max_price || (it->getPrice() == max_price && it->getId() < max_id))
        {
            max_price = it->getPrice();
            max_id = it->getId();
        }
    }

    for (it = workouts.begin(); it != workouts.end(); it++)
    {
        if (it->getId() == max_id)
        {
            return *it;
        }
    }
    return *it;
}

Customer::~Customer() {}
