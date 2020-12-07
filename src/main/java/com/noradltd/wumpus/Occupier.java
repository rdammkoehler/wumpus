package com.noradltd.wumpus;

import java.util.Map;

class Occupier implements Room.Occupant {
    private Room room;

    protected final Random randomizer = Random.getRandomizer();

    public Occupier() {
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public void moveTo(Room newRoom) {
        if (room != null ) {
            room.remove(this);
        }
        room = newRoom.add(this);
    }

    // Default
    @Override
    public void respondTo(Room.Occupant visitor) {
        System.out.println("Hello " + visitor.getClass().getSimpleName() + " I'm "+ this.getClass().getSimpleName());
    }

    @Override
    public int compareTo(Occupier o) {
        return this.getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
    }

}
