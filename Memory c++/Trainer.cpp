#include "Trainer.h"
#include <iostream>

using namespace std;

Trainer::Trainer(int t_capacity) : capacity(t_capacity)
{
    open = false;
    _salary = 0;
    _id = -1;
}

Trainer::Trainer(const Trainer &other) : capacity(other.getCapacity())
{
    open = other.open;
    _id = other.getId();
    _salary = other._salary;
    vector<Customer *>::const_iterator it;
    for (it = other.customersList.begin(); it < other.customersList.end(); it++)
    {
        customersList.push_back((*it)->getInstance());
    }

    vector<OrderPair>::const_iterator it2;
    for (it2 = other.orderList.begin(); it2 < other.orderList.end(); it2++)
    {
        orderList.push_back(*it2);
    }
}

Trainer::Trainer(Trainer &&other) : capacity(other.capacity), open(other.open), _id(other._id), _salary(other._salary)
{
    if (this != &other)
    {
        vector<OrderPair>::const_iterator it;
        for (it = other.orderList.begin(); it != other.orderList.end(); it++)
        {
            orderList.push_back(*it);
        }

        vector<Customer *>::const_iterator it2;
        for (it2 = other.customersList.begin(); it2 < other.customersList.end(); it2++)
        {
            customersList.push_back(*it2);
        }
        other.customersList.at(it2 - other.customersList.begin()) = nullptr;
    }
}

Trainer::~Trainer()
{
    Clear();
}

Trainer &Trainer::operator=(const Trainer &other)
{
    if (this != &other)
    {
        Clear();
        _id = other._id;
        _salary = other._salary;
        open = other.open;
        capacity = other.capacity;

        vector<OrderPair>::const_iterator it2;
        for (it2 = other.orderList.begin(); it2 < other.orderList.end(); it2++)
        {
            orderList.push_back(*it2);
        }

        vector<Customer *>::const_iterator it;
        for (it = other.customersList.begin(); it < other.customersList.end(); it++)
        {
            Customer *c(*it);
            customersList.push_back(c);
        }
    }
    return *this;
}

void Trainer::Clear()
{
    vector<Customer *>::iterator it;
    for (it = customersList.begin(); it != customersList.end(); it++)
    {
        delete (*it);
    }
    customersList.clear();
    orderList.clear();
}

Trainer &Trainer::operator=(Trainer &&other)
{
    if (this != &other)
    {
        Clear();
        _id = other._id;
        _salary = other._salary;
        open = other.open;
        capacity = other.capacity;
        vector<OrderPair>::const_iterator it;
        for (it = other.orderList.begin(); it != other.orderList.end(); it++)
        {
            orderList.push_back(*it);
        }
        vector<Customer *>::const_iterator it2;
        for (it2 = other.customersList.begin(); it2 != other.customersList.end(); it2++)
        {
            customersList.push_back(*it2);
            other.customersList.at(it2 - other.customersList.begin()) = nullptr;
        }
    }
    return *this;
}

int Trainer::getCapacity() const
{
    return capacity;
}

void Trainer::addCustomer(Customer *customer)
{
    if (customersList.size() <= (unsigned int)capacity + 1)
        customersList.push_back(customer);
}

void Trainer::removeCustomer(int id)
{
    vector<Customer *>::iterator it;
    vector<OrderPair> replace;
    for (it = customersList.begin(); it < customersList.end(); it++)
    {
        if ((*it)->getId() == id)
        {
            customersList.erase(it);
            vector<OrderPair>::iterator it2;
            for (it2 = orderList.begin(); it2 != orderList.end(); it2++)
            {
                if (it2->first != id)
                {
                    replace.push_back(*it2);
                }
                else
                    _salary -= it2->second.getPrice();
            }

            break;
        }
    }
    orderList.clear();
    vector<OrderPair>::iterator it3;
    for (it3 = replace.begin(); it3 != replace.end(); it3++)
    {
        orderList.push_back(*it3);
    }
}

Customer *Trainer::getCustomer(int id)
{
    vector<Customer *>::iterator it;
    for (it = customersList.begin(); it < customersList.end(); it++)
    {
        if ((*it)->getId() == id)
        {
            return *it;
        }
    }
    return nullptr;
}

std::vector<Customer *> &Trainer::getCustomers()
{
    return customersList;
}

std::vector<OrderPair> &Trainer::getOrders()
{
    return orderList;
}

void Trainer::order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout> &workout_options)
{
    vector<OrderPair> temp;
    vector<int>::const_iterator it;
    for (it = workout_ids.begin(); it != workout_ids.end(); it++)
    {
        vector<Workout>::const_iterator it2;
        for (it2 = workout_options.begin(); it2 != workout_options.end(); it2++)
        {
            if (it2->getId() == *it)
            {
                orderList.emplace_back(OrderPair(customer_id, *it2));
                temp.emplace_back(OrderPair(customer_id, *it2));
            }
        }
    }
    vector<OrderPair>::iterator it3;
    for (it3 = temp.begin(); it3 < temp.end(); it3++)
    {
        vector<Customer *>::iterator it4;
        for (it4 = customersList.begin(); it4 < customersList.end(); it4++)
        {
            if ((*it4)->getId() == it3->first)
            {
                cout << (*it4)->getName() + " Is Doing " + it3->second.getName() << endl;
                _salary += it3->second.getPrice();
                break;
            }
        }
    }
}

void Trainer::openTrainer()
{
    if (!open)
        open = true;
}

void Trainer::closeTrainer()
{
    if (open)
    {
        open = false;
        cout << "Trainer " << to_string(_id) << " closed. Salary " << _salary << "NIS" << endl;
        vector<Customer *>::iterator it;
        for (it = customersList.begin(); it != customersList.end(); it++)
        {
            delete *it;
        }
        customersList.clear();
        orderList.clear();
    }
}

int Trainer::getSalary()
{
    return _salary;
}

bool Trainer::isOpen()
{
    return open;
}

std::string Trainer::toString()
{
    string result = "Trainer " + to_string(_id) + " status: ";
    if (open)
    {
        result.append("open");
        if (!orderList.empty())
        {
            result.append("\nCustomers:\n");
            vector<Customer *>::const_iterator it;
            for (it = customersList.begin(); it < customersList.end(); it++)
            {
                result.append((*it)->toString() + "\n");
            }

            result.append("Orders:\n");

            vector<OrderPair>::iterator it2;
            for (it2 = orderList.begin(); it2 < orderList.end(); it2++)
            {
                result.append(it2->second.toString() + " " + to_string(it2->first) + "\n");
            }
            result.append("Current Trainer's Salary: " + to_string(_salary) + "NIS");
        }
    }
    else
    {
        result.append("closed");
    }
    return result;
}

int Trainer::getId() const
{
    return _id;
}

void Trainer::setId(int id)
{
    _id = id;
}

void Trainer::setSalary(int newSalary)
{
    _salary = newSalary;
}

void Trainer::addOrder(OrderPair order)
{
    orderList.emplace_back(order);
}