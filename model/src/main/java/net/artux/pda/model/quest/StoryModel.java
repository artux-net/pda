package net.artux.pda.model.quest;

import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class StoryModel implements Serializable {

    private Long id;
    private String title;
    private Map<Long, GameMap> maps;
    private Map<String, ChapterModel> chapters;
    private List<MissionModel> missions;

    public ChapterModel getChapter(String id) {
        return chapters.get(id);
    }

    public List<MissionModel> getCurrentMissions(String... params) {
        List<MissionModel> missionModels = new LinkedList<>();
        for (MissionModel missionModel : missions) {
            if (missionModel.hasParams(params))
                missionModels.add(missionModel);
        }
        return missionModels;
    }

    public MissionModel getCurrentMission(String param) {
        for (MissionModel missionModel : missions) {
            if (missionModel.hasParams(param))
                return missionModel;
        }
        return null;
    }

    public Point findPathWithinMission(String param, GameMap currentMap) {
        MissionModel missionModel = getCurrentMission(param);
        if (missionModel != null) {
            CheckpointModel checkpointModel = missionModel.getCurrentCheckpoint(param);
            Point targetPoint;
            GameMap targetMap;
            for (GameMap map : maps.values()) {
                for (Point point : map.getPoints()) {
                    Map<String, String> data = point.getData();
                    String chapterString = data.get("chapter");
                    String stageString = data.get("stage");
                    if (chapterString != null && stageString != null
                            && chapterString.equals(checkpointModel.getChapter().toString())
                            && stageString.equals(checkpointModel.getStage().toString())) {
                        targetMap = map;
                        targetPoint = point;
                    }
                }
            }


        }
        return null;
    }

}
