
package com.jfixby.r3.ui.red.activity;

import com.jfixby.r3.api.EngineAssembler;
import com.jfixby.r3.api.RedTriplane;
import com.jfixby.r3.api.activity.spawn.ActivityMachine;
import com.jfixby.r3.api.activity.spawn.ActivityMachineComponent;
import com.jfixby.r3.api.activity.spawn.Intent;
import com.jfixby.r3.api.activity.spawn.IntentStack;
import com.jfixby.r3.api.exe.EngineState;
import com.jfixby.scarabei.api.assets.ID;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.Queue;
import com.jfixby.scarabei.api.debug.Debug;
import com.jfixby.scarabei.api.log.L;
import com.jfixby.scarabei.api.sys.settings.SystemSettings;
import com.jfixby.scarabei.api.taskman.SysExecutor;
import com.jfixby.scarabei.api.ver.Version;

public class RedActivityMachine implements ActivityMachineComponent {

	final private EngineAssembler engine_assembler;
	private Queue<IntentContainer> queue;
	ActivityManager units_manager;

	RedActivityMachine (final EngineAssembler engine_assembler) {
		this.engine_assembler = engine_assembler;
	}

	public void doDeploy () {
		this.queue = Collections.newQueue();
		ActivityMachine.installComponent(this);

		if (this.engine_assembler != null) {
			this.engine_assembler.assembleEngine();
		}

		final String applicationPackageName = (SystemSettings.getStringParameter(Version.Tags.PackageName));
		final String versionCode = SystemSettings.getStringParameter(Version.Tags.VersionCode);
		final String versionName = SystemSettings.getStringParameter(Version.Tags.VersionName);

		Debug.checkNull("SystemSettings :: " + Version.Tags.PackageName, applicationPackageName);
		Debug.checkEmpty("SystemSettings :: " + Version.Tags.PackageName, applicationPackageName);

		Debug.checkNull("SystemSettings :: " + Version.Tags.VersionCode, versionCode);
		Debug.checkEmpty("SystemSettings :: " + Version.Tags.VersionCode, versionCode);

		Debug.checkNull("SystemSettings :: " + Version.Tags.VersionName, versionName);
		Debug.checkEmpty("SystemSettings :: " + Version.Tags.VersionName, versionName);

		L.d("------[RedTriplane Engine Start]---------------------------------------------------------");
		L.d("            version - " + RedTriplane.VERSION().getName());
		L.d("           build id - " + RedTriplane.VERSION().getBuildID());
		L.d("           homepage - " + RedTriplane.VERSION().getHomePage());
		L.d();
		L.d("        application - " + applicationPackageName);
		L.d("            version - " + versionName);
		L.d("            v. code - " + versionCode);
		L.d("-----------------------------------------------------------------------------------------");

		// Sys.exit();

		this.units_manager = new ActivityManager();

		SysExecutor.onSystemStart();

// RenderMachine.init();

		final ID starter = RedTriplane.getGameStarter();
		final Intent intent = ActivityMachine.newIntent(starter);
		this.nextActivity(intent);
// L.d("Screen dimensions", Screen.getScreenDimensions());

	}

	public void doDispose () {
		if (this.units_manager.isIdle()) {
			return;
		}
	}

	public void doPause () {
		if (this.units_manager.isIdle()) {
			return;
		}
		this.units_manager.suspend();
	}

	public void doRender (final EngineState engine_state) {
// if (this.queue.hasMore()) {
// final Intent intent = this.queue.pop().intent();
// this.units_namager.loadNext(intent);
// return;
// }
		if (this.units_manager.isIdle()) {
			return;
		}
		this.units_manager.render(engine_state);
	}

	public void doResume () {
		if (this.units_manager.isIdle()) {
			return;
		}
		this.units_manager.resume();
	}

	public void doUpdate (final EngineState engine_state) {
		if (this.queue.hasMore()) {
			final Intent intent = this.queue.dequeue().intent();
			this.units_manager.loadNext(intent);
			return;
		}
		if (this.units_manager.isIdle()) {
			return;
		}

		this.units_manager.update(engine_state);

	}

	@Override
	public Intent newIntent (final ID intent_id) {
		final IntentStack stack = new IntentStack();
		return new RedActivityMachineIntent(intent_id, stack);
	}

	@Override
	public void nextActivity (final Intent intent) {
		this.queue.enqueue(new IntentContainer(intent));
	}

}
