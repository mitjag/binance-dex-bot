package si.babypanda.binance.dex;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BinanceExplorerApi {
    
    @GET("/api/v1/txs")
    Call<TransactionPage> getTxs(@Query("txAsset") String txAsset, @Query("page") Integer page, @Query("rows") Integer rows);
    
}
