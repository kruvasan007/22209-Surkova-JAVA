package com.mygdx.game.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class SpawnPoint {
    private final ArrayList<Point> points = new ArrayList<>();


    public ArrayList<Point> getPoints() {
        return points;
    }
}
