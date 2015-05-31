package com.groceryreminder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Photo;
import se.walkercrou.places.Place;
import se.walkercrou.places.PlaceBuilder;
import se.walkercrou.places.Prediction;
import se.walkercrou.places.RequestHandler;
import se.walkercrou.places.exception.GooglePlacesException;

public class GooglePlacesFake implements GooglePlacesInterface {

    protected List<Place> placesResponse;

    public GooglePlacesFake() {
        placesResponse = new ArrayList<Place>();
    }

    public void setPlacesResponse(List<Place> places) {
        placesResponse = places;
    }

    @Override
    public boolean isDebugModeEnabled() {
        return false;
    }

    @Override
    public void setDebugModeEnabled(boolean b) {

    }

    @Override
    public String getApiKey() {
        return null;
    }

    @Override
    public void setApiKey(String s) {

    }

    @Override
    public RequestHandler getRequestHandler() {
        return null;
    }

    @Override
    public void setRequestHandler(RequestHandler requestHandler) {

    }

    @Override
    public List<Place> getNearbyPlaces(double v, double v1, double v2, int i, Param... params) {
        return placesResponse;
    }

    @Override
    public List<Place> getNearbyPlaces(double v, double v1, double v2, Param... params) {
        return placesResponse;
    }

    @Override
    public List<Place> getNearbyPlacesRankedByDistance(double v, double v1, int i, Param... params) throws GooglePlacesException {
        return placesResponse;
    }

    @Override
    public List<Place> getNearbyPlacesRankedByDistance(double v, double v1, Param... params) throws GooglePlacesException {
        return placesResponse;
    }

    @Override
    public List<Place> getPlacesByQuery(String s, int i, Param... params) {
        return placesResponse;
    }

    @Override
    public List<Place> getPlacesByQuery(String s, Param... params) {
        return placesResponse;
    }

    @Override
    public List<Place> getPlacesByRadar(double v, double v1, double v2, int i, Param... params) {
        return placesResponse;
    }

    @Override
    public List<Place> getPlacesByRadar(double v, double v1, double v2, Param... params) {
        return placesResponse;
    }

    @Override
    public Place getPlaceById(String s, Param... params) {
        return null;
    }

    @Override
    public Place addPlace(PlaceBuilder placeBuilder, boolean b, Param... params) {
        return null;
    }

    @Override
    public void deletePlaceById(String s, Param... params) {

    }

    @Override
    public void deletePlace(Place place, Param... params) {

    }

    @Override
    public InputStream download(String s) {
        return null;
    }

    @Override
    public InputStream downloadPhoto(Photo photo, int i, int i1, Param... params) {
        return null;
    }

    @Override
    public List<Prediction> getPlacePredictions(String s, int i, int i1, int i2, int i3, Param... params) {
        return null;
    }

    @Override
    public List<Prediction> getPlacePredictions(String s, int i, Param... params) {
        return null;
    }

    @Override
    public List<Prediction> getPlacePredictions(String s, Param... params) {
        return null;
    }

    @Override
    public List<Prediction> getQueryPredictions(String s, int i, int i1, int i2, int i3, Param... params) {
        return null;
    }

    @Override
    public List<Prediction> getQueryPredictions(String s, int i, Param... params) {
        return null;
    }

    @Override
    public List<Prediction> getQueryPredictions(String s, Param... params) {
        return null;
    }
}
