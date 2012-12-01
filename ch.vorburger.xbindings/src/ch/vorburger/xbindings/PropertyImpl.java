package ch.vorburger.xbindings;

class PropertyImpl<T> implements Property<T> {
	T value;
	ChangeListener cl;
	@Override public void set(T newValue) { value = newValue; if (cl != null) cl.changed(); }
	@Override public T get() { PropertyAccessTrackerUtil.INSTANCE.accessed(this); return value; }
	@Override public void setChangeListener(ChangeListener cl) {
		this.cl = cl;
	}
}