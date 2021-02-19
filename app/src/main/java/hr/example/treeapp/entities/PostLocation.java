package hr.example.treeapp.entities;

import hr.example.treeapp.addTree.LatLng;

public class PostLocation {
    public LatLng latLng;
    public String postId;

    public PostLocation(LatLng latLng, String postId){
        this.latLng=latLng;
        this.postId=postId;
    }
}
