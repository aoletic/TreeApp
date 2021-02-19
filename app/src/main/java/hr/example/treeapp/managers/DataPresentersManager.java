package hr.example.treeapp.managers;

import android.content.Context;

import com.example.core.DataPresenter;
import com.example.timeline.PostListFragment;

import java.util.ArrayList;
import java.util.List;

import hr.example.mapview.PostMapView;


public class DataPresentersManager {
    public List<DataPresenter> presenters = new ArrayList<>();
    public DataPresenter currentPresenter;
    private Context context;
    public DataPresenter firstPresenter;
    String timeline = "Timeline";
    String mapView = "Map View";

    public DataPresentersManager(Context context){
        this.context = context;
        loadPresenters();
    }

    private void loadPresenters() {
        presenters.add(new PostListFragment());
        presenters.add(new PostMapView());
        if(!presenters.isEmpty()){
            firstPresenter = presenters.get(0);
        }
        DataManager dataManager = DataManager.getInstance();
        if(firstPresenter!=null){
            String modul = firstPresenter.getModuleName(context);
            if(modul.equals(timeline)){
                dataManager.loadDataTimeline(firstPresenter, context);
            }
            else if(modul.equals(mapView)){
                dataManager.loadDataMap(firstPresenter, context);
            }
            else{
                dataManager.loadAllData(firstPresenter, context);
            }
        }

    }

    public void loadFragment(int i){
        DataManager dataManager = DataManager.getInstance();
        String modul = presenters.get(i).getModuleName(context);
        if(modul.equals(timeline)){
            dataManager.loadDataTimeline(presenters.get(i), context);
        }
        else if(modul.equals(mapView)){
            dataManager.loadDataMap(presenters.get(i), context);
        }
        else{
            dataManager.loadAllData(presenters.get(i), context);
        }
    }

    public void refreshDataTimeline(){
        DataManager dataManager = DataManager.getInstance();
        String requiredModuleName = "Timeline";
        int i = -1;
        int j = -1;
        for(DataPresenter dp : presenters){
            String moduleName = dp.getModuleName(context);
            j++;
            if(requiredModuleName.equals(moduleName)){
                i = j;
                break;
            }
        }
        if(i>=0) {
            dataManager.loadDataTimeline(presenters.get(i), context);
        }
    }
}
