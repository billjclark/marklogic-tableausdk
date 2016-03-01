(: XQuery main module :)
xquery version "1.0-ml";
declare namespace html = "http://www.w3.org/1999/xhtml";
(: Allow access to the appropriate requesting host :)
(:
let$_ := xdmp:add-response-header("Access-Control-Allow-Origin",xdmp:get-request-header("Origin","http://localhost:8000"))
let $type := map:get($params,"type")
let $_=xdmp:log("entering get here.")

xdmp:set-response-content-type("text/html"),
let $results := cts:search(fn:doc(), cts:collection-query("claims"))[1 to 100]
                   
return 
    let $json-results :=
        for $result in $results/claim
            let $json-result := json:object()
            let $_ := map:put($json-result, "id", fn:normalize-space($result/id/fn:data()))
            let $_ := map:put($json-result, "type", fn:normalize-space($result/type/fn:data()))
            let $_ := map:put($json-result, "ssn", fn:normalize-space($result/patient-ssn/fn:data()))
            let $_ := map:put($json-result, "claim_date", fn:normalize-space($result/to/fn:data()))
            let $_ := map:put($json-result, "payment_amount", fn:normalize-space($result/payment-amount/fn:data()))
        return 
          $json-result
    let $return-map := json:object()
    let $_ := map:put($return-map, "claims",$json-results)
    return
        xdmp:to-json($return-map)
