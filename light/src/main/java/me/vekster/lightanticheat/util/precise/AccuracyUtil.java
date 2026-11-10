package me.vekster.lightanticheat.util.precise;

import me.vekster.lightanticheat.check.CheckName;
import me.vekster.lightanticheat.check.CheckSetting;
import me.vekster.lightanticheat.check.buffer.Buffer;
import me.vekster.lightanticheat.version.identifier.LACVersion;
import me.vekster.lightanticheat.version.identifier.VerIdentifier;

public class AccuracyUtil {

    public static boolean isViolationCancel(CheckSetting checkSetting, Buffer buffer) {
        CheckName checkName = checkSetting.name;
        // Modified: the movement checks this covered were removed; only the 1.8 KillAura_B tolerance is left.
        if (!VerIdentifier.getVersion().isNewerThan(LACVersion.V1_8)
                && checkName == CheckName.KILLAURA_B && getRecentViolations(buffer) <= 1)
            return true;
        return false;
    }

    private static int getRecentViolations(Buffer buffer) {
        buffer.put("accuracyUtilViolations", buffer.getInt("accuracyUtilViolations") + 1);
        if (System.currentTimeMillis() - buffer.getLong("accuracyUtilTime") > 5000) {
            buffer.put("accuracyUtilTime", System.currentTimeMillis());
            buffer.put("accuracyUtilViolations", 1);
        }
        return buffer.getInt("accuracyUtilViolations");
    }

}
