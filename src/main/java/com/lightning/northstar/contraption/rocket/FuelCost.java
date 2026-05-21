package com.lightning.northstar.contraption.rocket;

public record FuelCost(
        float takeoff,
        float travel,
        float landing,
        float efficiency
) {

    public float total() {
        return (takeoff() + travel() + landing()) * efficiency();
    }

}
