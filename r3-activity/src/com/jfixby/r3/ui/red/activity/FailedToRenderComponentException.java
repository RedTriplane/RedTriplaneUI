
package com.jfixby.r3.ui.red.activity;

import com.jfixby.r3.ui.red.activity.layers.RedLayer;

public class FailedToRenderComponentException extends Error {

	public FailedToRenderComponentException (final Throwable xe, final RedLayer parent) {
		super(parent.toString(), xe);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 2889294867961737816L;

}
