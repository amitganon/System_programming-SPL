#include "Workout.h"
using namespace std;

Workout::Workout(int w_id, string w_name, int w_price, WorkoutType w_type) : id(w_id), name(w_name), price(w_price), type(w_type)
{}

Workout::Workout(const Workout& other) : id(other.id), name(other.name), price(other.price), type(other.type)
{}

int Workout::getId() const
{
    return id;
}

std::string Workout::getName() const
{
    return name;
}

int Workout::getPrice() const
{
    return price;
}


WorkoutType Workout::getType() const
{
    return type;
}

std::string Workout::toString()
{
    return name + " " + to_string(price) + "NIS";
}

string Workout::EnumToString(WorkoutType w)
{
    switch (w)
    {
    case ANAEROBIC:   return "Anaerobic";
    case MIXED:   return "Mixed";
    case CARDIO: return "Cardio";
    }
    return "";
}



