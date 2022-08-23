package com.noradltd.wumpus;

public interface RoomNameGenerator {
    RoomNameGenerator using(String[] possibilities);

    RoomNameGenerator sequentially();

    RoomNameGenerator randomly();

    String nextName();
}
