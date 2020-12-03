package com.noradltd.wumpus;

class Occupier implements Room.Occupant {
    protected Room room;

    public void moveTo(Room newRoom) {
        if (room != null ) {
            room.remove(this);
        }
        newRoom.add(this);
        room = newRoom;
    }

    @Override
    public int compareTo(Occupier o) {
        return this.getClass().getSimpleName().compareTo(o.getClass().getSimpleName());
    }

}
