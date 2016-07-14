package jp.tolz.migrationutils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * JavaScript�ɂ���Ė���_�����̐^�U������s���܂��B
 * �C�ӂ�JavaScript�����s�\�ł����A�Ō�ɎQ�Ƃ��ꂽ�I�u�W�F�N�g��boolean�^�łȂ��ƃG���[�ɂȂ�܂��B
 * @author �r�c ��
 */
public class BooleanExp {
	String question;
	private boolean answer;


	/**
	 * ����_�������쐬���܂��B
	 * @param question �^�U��Ԃ�JavaScript�\��
	 */
	public BooleanExp(String question) {
		this.question = question;
		preprocessing();
	}
	
	/**
	 * �X�N���v�g���s�O�̏������L�q���܂��B
	 * �f�t�H���g�ł͉����������s���܂���B
	 */
	void preprocessing(){
		
	}

	/**
	 * ex) new BooleanExp("1 > 100 or 9 > 4 and a = a").isTrue(); // true
	 */
	public boolean isTrue() throws Exception{
		runScript(question);
		return answer;
	}
	
	private void runScript(String js) throws Exception{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try{
		answer = (boolean) engine.eval(js);
		}catch(ScriptException e){
			throw new Exception("Incorrect Boolean Expression in JavaScript");
		}
	}

}