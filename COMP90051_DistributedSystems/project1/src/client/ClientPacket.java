/* ClientPacket.java
   Author: Thomas Choi 1202247 */

package client;

import shared.OperationCode;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class ClientPacket implements Serializable {
	private static final long serialVersionUID = 1L;
	private OperationCode code;
	private String word;
	private LinkedHashSet<String> meanings;
	
	public ClientPacket(OperationCode code, String word) {
		this.code = code;
		this.word = word;
	} 
	
	public ClientPacket(OperationCode code, String word, LinkedHashSet<String> meanings) {
		this.code = code;
		this.word = word;
		this.meanings = meanings;
	}
	
	public OperationCode getCode() {
		return code;
	}
	
	public String getWord() {
		return word;
	}
	
	public LinkedHashSet<String> getMeanings() {
		return meanings;
	}

}
