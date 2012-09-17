/*******************************************************************************
 * Copyright (c) 2012 Michael Vorburger (http://www.vorburger.ch).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package ch.vorburger.xtext.databinding.internal;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.IProperty;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.SimplePropertyEvent;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.databinding.internal.EMFPropertyListener;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IReadAccess;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * Like {@link EMFPropertyListener}, but {@link #addTo(Object)} works with Resource instead of EObject.
 * 
 * @author Michael Vorburger
 */
@SuppressWarnings("restriction")
public abstract class XtextPropertyListener extends EContentAdapter implements INativePropertyListener {
	// NOTE extends EContentAdapter, NOT EMFPropertyListener which extends AdapterImpl

	@SuppressWarnings("unchecked")
	protected Resource getResource(Object object) {
		IReadAccess<XtextResource> access = (IReadAccess<XtextResource>) object;
		return access.readOnly(new IUnitOfWork<Resource, XtextResource>() {
			@Override public Resource exec(XtextResource state) throws Exception {
	    		return state;
			}
		});
	}
	
	@Override
	public void addTo(Object source) {
		if (source != null) {
			getResource(source).eAdapters().add(this);
		}
	}

	@Override
	public void removeFrom(Object source) {
		if (source != null) {
			getResource(source).eAdapters().remove(this);
		}
	}

	@Override
	public abstract void notifyChanged(Notification msg);

	protected abstract ISimplePropertyListener getListener();

	protected abstract EStructuralFeature getFeature();

	protected abstract IProperty getOwner();

	
	public abstract static class XtextValuePropertyListener extends XtextPropertyListener {
		// TODO Carefully test if this will really work for FeaturePath as well, not just for one root EStructuralFeature...
		@Override
		public void notifyChanged(Notification msg) {
			System.out.println(msg); // TODO Remove Dev only System.out.println used to learn
			if (msg.isTouch())
				return;
			// TODO rewrite this more nicely.. use switch case? move out shareable code.
			// TODO how to check to make sure it is this property that is affected and not another?
			if (msg.getEventType() == Notification.REMOVE) {
				if (msg.getOldValue() instanceof EObject) {
					EObject oldEObject = (EObject) msg.getOldValue();
					sendChangeEvent(msg, oldEObject.eGet(getFeature()), null);
				}
			} else if (msg.getEventType() == Notification.ADD) {
				if (msg.getNewValue() instanceof EObject) {
					EObject newEObject = (EObject) msg.getNewValue();
					sendChangeEvent(msg, null, newEObject.eGet(getFeature()));
				}
			} else if (msg.getEventType() == Notification.SET) {
				// TODO Must handle this!
			}
			// OK to ignore Notification.REMOVE_MANY
		}
		
		protected void sendChangeEvent(Notification msg, Object oldValue, Object newValue) {
			getListener().handleEvent(
					new SimplePropertyEvent(SimplePropertyEvent.CHANGE,
							msg.getNotifier(), getOwner(), 
							Diffs.createValueDiff(oldValue, newValue)));
		}
	}
}