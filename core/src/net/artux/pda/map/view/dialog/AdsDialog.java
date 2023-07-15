package net.artux.pda.map.view.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.view.root.UserInterface;
import net.artux.pda.map.view.button.PDAButton;

import java.util.Collections;

import javax.inject.Inject;

public class AdsDialog extends PDADialog {

    @Inject
    public AdsDialog(PDAButton usualAd, PDAButton videoAd,
                     UserInterface userInterface,
                     DataRepository dataRepository,
                     LocaleBundle localeBundle) {
        super(localeBundle.get("main.ad.rewarded"), userInterface.getSkin());

        usualAd.setText(localeBundle.get("main.ad.rewarded.usual"));
        usualAd.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dataRepository.applyActions(Collections.singletonMap("showAd", Collections.singletonList("usual")), true);
            }
        });

        videoAd.setText(localeBundle.get("main.ad.rewarded.video"));
        videoAd.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dataRepository.applyActions(Collections.singletonMap("showAd", Collections.singletonList("video")), true);
            }
        });

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.center();
        horizontalGroup.fill().expand().pad(10);
        horizontalGroup.addActor(usualAd);
        horizontalGroup.addActor(videoAd);
        add(horizontalGroup);
    }

}
