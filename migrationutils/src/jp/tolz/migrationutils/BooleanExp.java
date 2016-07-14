package jp.tolz.migrationutils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * JavaScriptによって命題論理式の真偽判定を行います。
 * 任意のJavaScriptが実行可能ですが、最後に参照されたオブジェクトがboolean型でないとエラーになります。
 * @author 池田 透
 */
public class BooleanExp {
	String question;
	private boolean answer;


	/**
	 * 命題論理式を作成します。
	 * @param question 真偽を返すJavaScript表現
	 */
	public BooleanExp(String question) {
		this.question = question;
		preprocessing();
	}
	
	/**
	 * スクリプト実行前の処理を記述します。
	 * デフォルトでは何も処理を行いません。
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