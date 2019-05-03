package cnge.graphics.sound;

import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

public class ALManagement {

	private long device, context;
	private ALCCapabilities deviceCaps;
	
	public ALManagement() {
		device = alcOpenDevice((ByteBuffer) null);
		if (device == 0)
			throw new IllegalStateException("Failed to open the default device.");
	
		deviceCaps = ALC.createCapabilities(device);
	
		context = alcCreateContext(device, (IntBuffer) null);
		if (context == 0)
		 throw new IllegalStateException("Failed to create an OpenAL context.");
		
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}
	
	public void destroy() {
		ALC10.alcDestroyContext(context);
		ALC10.alcCloseDevice(device);
	}
}