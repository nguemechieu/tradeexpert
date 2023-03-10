package org.tradeexpert.tradeexpert;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.paint.Color.rgb;
/**
 * @author Nguemechieu Noel Martial
 */
public class ChartColors {

    //CandleSticks chart colors
    public static final Paint CANDLE_BORDER_COLOR = rgb(55, 255, 255,1);
    public static final Paint CANDLE_FILL_COLOR = rgb(185, 165, 185, 1.00);
    public static final Paint CANDLE_STROKE_COLOR = Color.GOLD;

    public static Paint BEAR_CANDLE_BORDER_COLOR = Color.RED;
    public static final Paint BEAR_CANDLE_FILL_COLOR = TRANSPARENT;
    public static final Paint BULL_CANDLE_BORDER_COLOR = Color.GREEN;
    public static final Paint BULL_CANDLE_FILL_COLOR = TRANSPARENT;
    public static final Paint PLACE_HOLDER_FILL_COLOR = Color.GREEN;
    public static final Paint PLACE_HOLDER_BORDER_COLOR = Color.TRANSPARENT;
    public static final Paint AXIS_TICK_LABEL_COLOR = rgb(54, 234, 197);

    private ChartColors() {
    }
}

