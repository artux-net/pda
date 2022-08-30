package net.artux.pda.model.items;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetectorModel extends WearableModel {

    private DetectorType detectorType;

}
