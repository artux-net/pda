package net.artux.pda.ui.fragments.profile.helpers;

import android.content.Context;

import net.artux.pda.R;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Artifact;
import net.artux.pdalib.profile.items.Detector;
import net.artux.pdalib.profile.items.Item;
import net.artux.pdalib.profile.items.Weapon;

import java.text.DecimalFormat;

public class ItemsHelper {

    private static DecimalFormat decimalFormat = new DecimalFormat("###.##");

    private static String getDefault(Item item, Context context){
        return context.getString(R.string.item_desc, String.valueOf(item.price),
                String.valueOf(item.weight), String.valueOf(item.quantity), decimalFormat.format(item.quantity*item.weight));
    }

    public static String getDesc(Item item, Context context) {
        String result = getDefault(item, context);

        if (item instanceof Armor){
            Armor armor = (Armor) item;
            result += context.getString(R.string.armor_desc, String.valueOf(armor.thermal_pr),
                    String.valueOf(armor.electric_pr), String.valueOf(armor.chemical_pr), String.valueOf(armor.radio_pr),
                    String.valueOf(armor.psy_pr), String.valueOf(armor.damage_pr), String.valueOf(armor.condition));
        }else if(item instanceof Artifact){
            Artifact artifact = (Artifact) item;
            context.getString(R.string.artifact_desc, String.valueOf(artifact.health),
                    String.valueOf(artifact.radio), String.valueOf(artifact.damage), String.valueOf(artifact.bleeding),
                    String.valueOf(artifact.thermal), String.valueOf(artifact.chemical), String.valueOf(artifact.endurance),
                    String.valueOf(artifact.electric));
        }else if (item instanceof Detector){
            //TODO
        }else if (item instanceof Weapon){
            Weapon weapon = (Weapon) item;
            context.getString(R.string.weapon_desc, String.valueOf(weapon.precision),
                    String.valueOf(weapon.speed), String.valueOf(weapon.damage), String.valueOf(weapon.condition));
        }
        return result;
    }
}
