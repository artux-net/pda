package net.artux.pda.ui.fragments.profile.helpers;

import android.content.Context;

import net.artux.pda.R;
import net.artux.pda.map.models.items.Armor;
import net.artux.pda.map.models.items.Artifact;
import net.artux.pda.map.models.items.Detector;
import net.artux.pda.map.models.items.Item;
import net.artux.pda.map.models.items.Weapon;

import java.text.DecimalFormat;

public class ItemsHelper {

    private static DecimalFormat decimalFormat = new DecimalFormat("###.##");

    private static String getDefault(Item item, Context context){
        return context.getString(R.string.item_desc, String.valueOf(item.getPrice()),
                String.valueOf(item.getWeight()), String.valueOf(item.getQuantity()),
                decimalFormat.format(item.getQuantity()*item.getWeight()));
    }

    public static String getDesc(Item item, Context context) {
        String result = getDefault(item, context);

        if (item instanceof Armor){
            Armor armor = (Armor) item;
            result += context.getString(R.string.armor_desc,
                    String.valueOf(armor.getThermal_pr()),
                    String.valueOf(armor.getElectric_pr()),
                    String.valueOf(armor.getChemical_pr()),
                    String.valueOf(armor.getRadio_pr()),
                    String.valueOf(armor.getPsy_pr()),
                    String.valueOf(armor.getDamage_pr()),
                    String.valueOf(armor.getCondition()));
        }else if(item instanceof Artifact){
            Artifact artifact = (Artifact) item;
            context.getString(R.string.artifact_desc,
                    String.valueOf(artifact.getHealth()),
                    String.valueOf(artifact.getRadio()),
                    String.valueOf(artifact.getDamage()),
                    String.valueOf(artifact.getBleeding()),
                    String.valueOf(artifact.getThermal()),
                    String.valueOf(artifact.getChemical()),
                    String.valueOf(artifact.getEndurance()),
                    String.valueOf(artifact.getElectric()));
        }else if (item instanceof Detector){
            //TODO
        }else if (item instanceof Weapon){
            Weapon weapon = (Weapon) item;
            context.getString(R.string.weapon_desc,
                    String.valueOf(weapon.getPrecision()),
                    String.valueOf(weapon.getSpeed()),
                    String.valueOf(weapon.getDamage()),
                    String.valueOf(weapon.getCondition()));
        }
        return result;
    }
}
