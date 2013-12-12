import org.json.JSONObject;
import com.pubnub.api.MtGox;
import com.pubnub.api.MtGoxCallback;

public class PubnubMtGoxSample {

	public static void main(String[] args) {
		MtGox mtgx = new MtGox();
		
		mtgx.subscribe("ticker.BTCUSD", new MtGoxCallback(){

			@Override
			public void callback(JSONObject data) {
				System.out.println(data.getClass() + " : " + data);
				
			}});
	}
}

