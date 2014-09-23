/**
 * 
 */
package com.crm.cgw.submodifytcp;

/**
 * @author hungdt
 * 
 */
public class ChangeState extends Charging {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String	state	= "";

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setContent(String strContent) throws Exception {
		super.setContent(strContent);
		String[] content = strContent.split(SEPARATE_CHAR);

		try {
			setId(Integer.parseInt(content[0].trim()));
			setAccount(content[1].trim().split("=")[1]);
			setMdn(content[2].trim().split("=")[1]);
			setCharggw_type(TYPE_CHANGE_STATE);
			setState(content[3].trim().split("=")[1]);
			setComment(content[4].trim().split("=")[1]);
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public String toString()
	{
		String returnStr  = getCharg_seq() + "," + getId() + "," + getAccount() +"," +getMdn() + "," + 
								getCharggw_type() + ","+getState() + "," + getComment();
		return returnStr;
	}
}
