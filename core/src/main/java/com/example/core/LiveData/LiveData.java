package com.example.core.LiveData;

import com.example.core.VisibleMapRange;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.core.VisibleMapRange;

public class LiveData extends ViewModel {
    private static MutableLiveData<String> lastPostID;
    private static MutableLiveData<String> selectedPostId;
    private static MutableLiveData<VisibleMapRange> visibleMapRange;

    public static MutableLiveData<String> lastPostID() {
        if (lastPostID == null) {
            lastPostID = new MutableLiveData<String>();
        }
        return lastPostID;
    }

    public void UpdateLastPostNumber(String ID){
        this.lastPostID().setValue(ID);
    }

    public static MutableLiveData<String> selectedPostId() {
        if (selectedPostId == null) {
            selectedPostId = new MutableLiveData<String>();
        }
        return selectedPostId;
    }

    public void UpdateSelectedPostId(String postId){ this.selectedPostId().setValue(postId);
    }

    public static MutableLiveData<VisibleMapRange> visibleMapRange() {
        if (visibleMapRange == null) {
            visibleMapRange = new MutableLiveData<VisibleMapRange>();
        }
        return visibleMapRange;
    }
    public void UpdateVisibleMapRange(VisibleMapRange mapRange){ this.visibleMapRange().setValue(mapRange);
    }
}
