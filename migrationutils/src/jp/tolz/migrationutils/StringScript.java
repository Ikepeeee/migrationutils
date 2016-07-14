package jp.tolz.migrationutils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * JavaScript�ɂ���ĕ�����̍쐬���s���܂��B
 * �C�ӂ�JavaScript�����s�\�ł����A�Ō�ɎQ�Ƃ��ꂽ�I�u�W�F�N�g��String�^�łȂ��ƃG���[�ɂȂ�܂��B
 * @author �r�c ��
 */
public class StringScript {
	String script;
	private String javaStr;


	/**
	 * ������쐬�p�X�N���v�g���쐬���܂��B
	 * @param script ������ԋp����JavaScript
	 */
	public StringScript(String script) {
		this.script = script;
		preprocessing();
	}
	
	/**
	 * �X�N���v�g���s�O�̏������L�q���܂��B
	 * �f�t�H���g�ł͉����������s���܂���B
	 */
	void preprocessing(){
		
	}

	/**
	 * JavaScript��Java��String�ɕϊ����܂��B
	 * @return
	 * @throws Exception
	 */
	public String toJavaString() throws Exception{
		runScript(script);
		return javaStr;
	}
	
	private void runScript(String js) throws Exception{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try{
		javaStr = (String) engine.eval(js);
		}catch(ScriptException e){
			throw new Exception("Incorrect String Expression in JavaScript");
		}
	}

}