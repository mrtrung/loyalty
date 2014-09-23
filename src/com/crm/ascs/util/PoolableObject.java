package com.crm.ascs.util;

public interface PoolableObject
{
	public void activate();
	
	public void destroy();
	
	public void passivate();
	
	public boolean validate();
}
