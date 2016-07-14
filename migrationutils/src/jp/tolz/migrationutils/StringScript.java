package jp.tolz.migrationutils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * JavaScriptによって文字列の作成を行います。
 * 任意のJavaScriptが実行可能ですが、最後に参照されたオブジェクトがString型でないとエラーになります。
 * @author 池田 透
 */
public class StringScript {
	String script;
	private String javaStr;


	/**
	 * 文字列作成用スクリプトを作成します。
	 * @param script 文字を返却するJavaScript
	 */
	public StringScript(String script) {
		this.script = script;
		preprocessing();
	}
	
	/**
	 * スクリプト実行前の処理を記述します。
	 * デフォルトでは何も処理を行いません。
	 */
	void preprocessing(){
		
	}

	/**
	 * JavaScriptをJavaのStringに変換します。
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