package com.noradltd.wumpus;

abstract class Occupier implements Room.Occupant {
    private Room room;
    private Boolean dead = Boolean.FALSE;

    public Occupier() {
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public void moveTo(Room newRoom) {
        if (room != null) {
            room.remove(this);
        }
        room = newRoom.add(this);
    }

    @Override
    public Boolean isDead() {
        return dead;
    }

    protected void setDead(Boolean dead) {
        this.dead = dead;
    }

    @Override
    public int compareTo(Occupier other) {
        return this.getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
    }

}
