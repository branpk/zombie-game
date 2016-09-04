package game;

public final class Timer {
	private long startTime;

	public Timer() {
		restart();
	}
	
	public void restart() {
		startTime = System.nanoTime();
	}
	
	public DeltaTime elapsed() {
		long currentTime = System.nanoTime();
		return new DeltaTime(currentTime - startTime);
	}
	
	public DeltaTime tick() {
		DeltaTime dt = elapsed();
		restart();
		return dt;
	}
	
	public static class DeltaTime {
		private long nanoTime;
		
		public DeltaTime(long nanoTime) {
			this.nanoTime = nanoTime;
		}
		
		public long inNanos() {
			return nanoTime;
		}
		
		public double inMicros() {
			return nanoTime/1000.0;
		}
		
		public double inMillis() {
			return nanoTime/1000000.0;
		}
		
		public double inSecs() {
			return nanoTime/1000000000.0;
		}
	}
}
