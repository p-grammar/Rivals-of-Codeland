package cnge.core;

public class Timer {

	private double time;
	private double timer;
	private boolean going;

	public Timer(double t) {
		time = t;
		timer = 0;
		going = false;
	}

	public void start() {
		timer = 0;
		going = true;
	}

	public void end() {
		timer = time;
		going = false;
	}

	public void reset() {
		timer = 0;
		going = false;
	}

	public void setTime(double t) {
		time = t;
	}

	public void setTimer(double t) {
		timer = t;
	}

	public void setGoing(boolean g) {
		going = g;
	}

	public boolean update(double step) {
		if(going) {
			timer += step;
			if(timer >= time) {
				end();
				return true;
			}
		}
		return false;
	}

	public boolean continualUpdate(double step) {
		if(going) {
			timer += step;
			boolean ret = timer >= time;
			timer %= time;
			return ret;
		}
		return false;
	}

	public double getAlong() {
		return timer / time;
	}

	public float getInterp(Interpolator i) {
		return i.interpolate(0, time, getAlong());
	}

	public double getTime() {
		return time;
	}

	public double getTimer() {
		return timer;
	}

	//
	//interpolators
	//

	public interface Interpolator {
		float interpolate(double start, double end, double along);
	}

	public static float betweenValues(double start, double end, double interpolated) {
		return (float)((end - start) * interpolated + start);
	}

	public static final Interpolator LINEAR = (start, end, along) ->
			betweenValues(start, end, along);

	public static final Interpolator SQUARE = (start, end, along) ->
			betweenValues(start, end, (float)Math.pow(along, 2));

	public static final Interpolator ROOT = (start, end, along) ->
			betweenValues(start, end, (float)Math.sqrt(along));

	public static final Interpolator UNDERCIRCLE = (start, end, along) ->
			betweenValues(start, end, 1 - (float)Math.sqrt(1 - Math.pow(along, 2)));

	public static final Interpolator OVERCIRCLE = (start, end, along) ->
			betweenValues(start, end, (float)Math.sqrt(1 - Math.pow(along - 1, 2)));

	public static final Interpolator COSINE = (start, end, along) ->
			betweenValues(start, end, (float)( (Math.cos(Math.PI * (along - 1)) + 1) / 2));

}
