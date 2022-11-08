package net.artux.pda.ui.fragments.profile.helpers;

import android.content.Context;

import net.artux.pda.R;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ArtifactModel;
import net.artux.pda.model.items.DetectorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WeaponModel;

import java.text.DecimalFormat;

public class ItemsHelper {

    private static final DecimalFormat decimalFormat = new DecimalFormat("###.##");

    private static String getDefault(ItemModel item, Context context) {
        return context.getString(R.string.item_desc, String.valueOf(item.getPrice()),
                String.valueOf(item.getWeight()), String.valueOf(item.getQuantity()),
                decimalFormat.format(item.getQuantity() * item.getWeight()));
    }

    public static String getDesc(ItemModel item, Context context) {
        String result = getDefault(item, context);

        if (item instanceof ArmorModel) {
            ArmorModel armor = (ArmorModel) item;
            result += context.getString(R.string.armor_desc,
                    String.valueOf(armor.getThermal_pr()),
                    String.valueOf(armor.getElectric_pr()),
                    String.valueOf(armor.getChemical_pr()),
                    String.valueOf(armor.getRadio_pr()),
                    String.valueOf(armor.getPsy_pr()),
                    String.valueOf(armor.getDamage_pr()),
                    String.valueOf(armor.getCondition()));
        } else if (item instanceof ArtifactModel) {
            ArtifactModel artifact = (ArtifactModel) item;
            context.getString(R.string.artifact_desc,
                    String.valueOf(artifact.getHealth()),
                    String.valueOf(artifact.getRadio()),
                    String.valueOf(artifact.getDamage()),
                    String.valueOf(artifact.getBleeding()),
                    String.valueOf(artifact.getThermal()),
                    String.valueOf(artifact.getChemical()),
                    String.valueOf(artifact.getEndurance()),
                    String.valueOf(artifact.getElectric()));
        } else if (item instanceof DetectorModel) {
            DetectorModel detectorModel = (DetectorModel) item;
            context.getString(R.string.detector_desc,
                    String.valueOf(detectorModel.getDetectorType().toString()));
        } else if (item instanceof WeaponModel) {
            WeaponModel weapon = (WeaponModel) item;
            context.getString(R.string.weapon_desc,
                    String.valueOf(weapon.getPrecision()),
                    String.valueOf(weapon.getSpeed()),
                    String.valueOf(weapon.getDamage()),
                    String.valueOf(weapon.getCondition()));
        }
        return result;
    }
}
