"""
Admin API functional test suite — checks error codes in JSON body, not HTTP status.
"""
import urllib.request
import urllib.error
import json
import sys
import urllib.parse

BASE = "http://localhost:8080"
TOKEN = None
PASS = 0
FAIL = 0


def req(method, path, data=None, params=None, auth=True):
    url = BASE + path
    if params:
        qs_parts = []
        for k, v in params.items():
            if v:
                qs_parts.append(f"{urllib.parse.quote(k)}={urllib.parse.quote(v)}")
        if qs_parts:
            url += "?" + "&".join(qs_parts)

    body_bytes = None
    if data:
        body_bytes = json.dumps(data).encode("utf-8")

    rq = urllib.request.Request(url, data=body_bytes, method=method)
    rq.add_header("Content-Type", "application/json")
    if auth and TOKEN:
        rq.add_header("admin_token", TOKEN)

    try:
        with urllib.request.urlopen(rq, timeout=15) as resp:
            return resp.status, json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        try:
            return e.code, json.loads(e.read().decode())
        except Exception:
            return e.code, {}
    except Exception as e:
        return 0, {"_error": str(e)}


def check(label, body, has_fields=None, no_fields=None, json_code=200):
    """All responses return HTTP 200. Check body.code for business result."""
    global PASS, FAIL
    ok = True
    issues = []

    bcode = body.get("code") if isinstance(body, dict) else None

    if bcode != json_code:
        ok = False
        issues.append(f"json.code={bcode} (expected {json_code})")

    data = body.get("data") if isinstance(body, dict) else None

    if ok and has_fields and data:
        item = data
        if isinstance(data, list):
            item = data[0] if data else {}
        if isinstance(item, dict):
            for f in has_fields:
                if f not in item:
                    ok = False
                    issues.append(f"missing field '{f}'")
                    break

    if ok and no_fields and data:
        item = data
        if isinstance(data, list):
            item = data[0] if data else {}
        if isinstance(item, dict):
            for f in no_fields:
                if f in item:
                    ok = False
                    issues.append(f"should not contain '{f}'")
                    break

    if ok:
        PASS += 1
        print(f"  [PASS] {label}")
    else:
        FAIL += 1
        print(f"  [FAIL] {label}  — {'; '.join(issues)}")
        snippet = json.dumps(body, ensure_ascii=False)[:200]
        print(f"         {snippet}")


def run():
    global TOKEN, PASS, FAIL

    # ── 1. Auth ─────────────────────────────────────────────
    print("\n═══ Auth ═══")

    _, b = req("POST", "/admin/login", data={"userName": "admin", "password": "newpass123"}, auth=False)
    check("POST /admin/login", b, has_fields=["token", "id", "userName"])
    TOKEN = b.get("data", {}).get("token", "")

    _, b = req("POST", "/admin/updatePwd", data={"id": 1, "password": "newpass123"})
    check("POST /admin/updatePwd", b)

    _, b = req("GET", "/admin/logout")
    check("GET  /admin/logout", b)

    # re-login
    _, b = req("POST", "/admin/login", data={"userName": "admin", "password": "newpass123"}, auth=False)
    TOKEN = b.get("data", {}).get("token", "")

    _, b = req("GET", "/admin/getAll")
    check("GET  /admin/getAll", b, has_fields=["id", "userName"])

    # ── 2. Buyer Management ─────────────────────────────────
    print("\n═══ Buyer Management ═══")

    _, b = req("GET", "/admin/buyer/list")
    check("GET  /admin/buyer/list", b, has_fields=["id", "userName", "name"], no_fields=["password"])

    _, b = req("GET", "/admin/buyer/1")
    check("GET  /admin/buyer/1", b, has_fields=["id", "userName"], no_fields=["password"])

    _, b = req("GET", "/admin/buyer/99999")
    check("GET  /admin/buyer/99999 (not exist)", b, json_code=1101)

    _, b = req("GET", "/admin/buyer/query", params={"status": "active"})
    check("GET  /admin/buyer/query?status=active", b, has_fields=["id"])

    _, b = req("GET", "/admin/buyer/query", params={"sex": "female", "status": "active"})
    check("GET  /admin/buyer/query?sex=female&status=active", b)

    _, b = req("GET", "/admin/buyer/query", params={"userName": "buyer"})
    check("GET  /admin/buyer/query?userName=buyer (fuzzy)", b)

    # Ban / unban
    _, b = req("GET", "/admin/buyer/query", params={"status": "active"})
    buyers = b.get("data", [])
    if buyers:
        bid = buyers[0]["id"]
        _, b = req("PUT", f"/admin/buyer/{bid}/ban")
        check(f"PUT  /admin/buyer/{bid}/ban", b)

        _, b = req("PUT", f"/admin/buyer/{bid}/ban")
        check(f"PUT  /admin/buyer/{bid}/ban (already banned)", b, json_code=1004)

        _, b = req("PUT", f"/admin/buyer/{bid}/unban")
        check(f"PUT  /admin/buyer/{bid}/unban", b)

        _, b = req("PUT", f"/admin/buyer/{bid}/unban")
        check(f"PUT  /admin/buyer/{bid}/unban (not locked)", b, json_code=4003)
    else:
        print("  [SKIP] buyer ban/unban — no active buyers")

    # ── 3. Seller Management ────────────────────────────────
    print("\n═══ Seller Management ═══")

    _, b = req("GET", "/admin/seller/list")
    check("GET  /admin/seller/list", b, has_fields=["id", "userName", "storeName"], no_fields=["password"])

    _, b = req("GET", "/admin/seller/1")
    check("GET  /admin/seller/1", b, has_fields=["id", "storeName"], no_fields=["password"])

    _, b = req("GET", "/admin/seller/99999")
    check("GET  /admin/seller/99999 (not exist)", b, json_code=1201)

    _, b = req("GET", "/admin/seller/query", params={"status": "active"})
    check("GET  /admin/seller/query?status=active", b, has_fields=["id"])

    _, b = req("GET", "/admin/seller/query", params={"storeCategory": "clothing"})
    check("GET  /admin/seller/query?storeCategory=clothing", b)

    _, b = req("GET", "/admin/seller/query", params={"storeName": "dian"})
    check("GET  /admin/seller/query?storeName=dian (fuzzy)", b)

    # Ban / unban
    _, b = req("GET", "/admin/seller/query", params={"status": "active"})
    sellers = b.get("data", [])
    if sellers:
        sid = sellers[0]["id"]
        _, b = req("PUT", f"/admin/seller/{sid}/ban")
        check(f"PUT  /admin/seller/{sid}/ban", b)

        _, b = req("PUT", f"/admin/seller/{sid}/ban")
        check(f"PUT  /admin/seller/{sid}/ban (already banned)", b, json_code=1004)

        _, b = req("PUT", f"/admin/seller/{sid}/unban")
        check(f"PUT  /admin/seller/{sid}/unban", b)
    else:
        print("  [SKIP] seller ban/unban — no active sellers")

    # ── 4. Product Management ───────────────────────────────
    print("\n═══ Product Management ═══")

    _, b = req("GET", "/admin/product/list")
    check("GET  /admin/product/list", b, has_fields=["id", "name", "price", "status"])

    _, b = req("GET", "/admin/product/1")
    check("GET  /admin/product/1", b, has_fields=["id", "name", "sellerName", "storeName"])

    _, b = req("GET", "/admin/product/99999")
    check("GET  /admin/product/99999 (not exist)", b, json_code=4001)

    _, b = req("GET", "/admin/product/query", params={"status": "pending_review"})
    check("GET  /admin/product/query?status=pending_review", b)

    _, b = req("GET", "/admin/product/query", params={"minPrice": "50", "maxPrice": "200"})
    check("GET  /admin/product/query?minPrice=50&maxPrice=200", b)

    _, b = req("GET", "/admin/product/query", params={"category": "electronics"})
    check("GET  /admin/product/query?category=electronics", b)

    # Approve
    _, b = req("GET", "/admin/product/query", params={"status": "pending_review"})
    pending = b.get("data", [])
    if pending:
        pid = pending[0]["id"]
        _, b = req("PUT", f"/admin/product/{pid}/approve")
        check(f"PUT  /admin/product/{pid}/approve", b)

        _, b = req("PUT", f"/admin/product/{pid}/approve")
        check(f"PUT  /admin/product/{pid}/approve (not pending)", b, json_code=4003)
    else:
        print("  [SKIP] product approve — no pending_review products")

    # Reject
    _, b = req("GET", "/admin/product/query", params={"status": "pending_review"})
    pending = b.get("data", [])
    if pending:
        pid = pending[0]["id"]
        _, b = req("PUT", f"/admin/product/{pid}/reject", data={"rejectReason": "auto test"})
        check(f"PUT  /admin/product/{pid}/reject", b)
    else:
        print("  [SKIP] product reject — no pending_review products")

    # Suspend
    _, b = req("GET", "/admin/product/query", params={"status": "onsale"})
    onsale = b.get("data", [])
    if onsale:
        pid = onsale[0]["id"]
        _, b = req("PUT", f"/admin/product/{pid}/suspend", data={"reason": "auto test"})
        check(f"PUT  /admin/product/{pid}/suspend", b)

        _, b = req("GET", f"/admin/product/{pid}")
        check(f"GET  /admin/product/{pid} (after suspend)", b)
    else:
        print("  [SKIP] product suspend — no onsale products")

    # ── 5. Order Management ─────────────────────────────────
    print("\n═══ Order Management ═══")

    _, b = req("GET", "/admin/order/list")
    check("GET  /admin/order/list", b, has_fields=["id", "orderNo", "amount", "status"])

    orders = b.get("data", [])
    if orders:
        oid = orders[0]["id"]
        _, b = req("GET", f"/admin/order/{oid}")
        check(f"GET  /admin/order/{oid}", b, has_fields=["id", "orderNo", "orderItems"])

    _, b = req("GET", "/admin/order/99999")
    check("GET  /admin/order/99999 (not exist)", b, json_code=6001)

    _, b = req("GET", "/admin/order/query", params={"status": "completed"})
    check("GET  /admin/order/query?status=completed", b)

    _, b = req("GET", "/admin/order/query", params={"status": "abnormal"})
    check("GET  /admin/order/query?status=abnormal", b)

    # Force close
    _, b = req("GET", "/admin/order/query", params={"status": "pending"})
    pending_orders = b.get("data", [])
    if pending_orders:
        oid = pending_orders[0]["id"]
        _, b = req("PUT", f"/admin/order/{oid}/forceClose", data={"remark": "auto test"})
        check(f"PUT  /admin/order/{oid}/forceClose", b)

        _, b = req("PUT", f"/admin/order/{oid}/forceClose")
        check(f"PUT  /admin/order/{oid}/forceClose (final status)", b, json_code=6003)
    else:
        # Try paid or processing orders
        for st in ("paid", "processing", "shipped"):
            _, b = req("GET", "/admin/order/query", params={"status": st})
            orders = b.get("data", [])
            if orders:
                oid = orders[0]["id"]
                break
        else:
            oid = None

        if oid:
            _, b = req("PUT", f"/admin/order/{oid}/forceClose", data={"remark": "auto test"})
            check(f"PUT  /admin/order/{oid}/forceClose", b)
        else:
            print("  [SKIP] order forceClose — no closable orders")

    # Handle abnormal
    _, b = req("GET", "/admin/order/query", params={"status": "abnormal"})
    abnormal = b.get("data", [])
    if abnormal:
        oid = abnormal[0]["id"]
        _, b = req("PUT", f"/admin/order/{oid}/handleAbnormal",
                   data={"handleResult": "refunded", "remark": "auto test"})
        check(f"PUT  /admin/order/{oid}/handleAbnormal (refunded)", b)

        _, b = req("PUT", f"/admin/order/{oid}/handleAbnormal",
                   data={"handleResult": "completed"})
        check(f"PUT  /admin/order/{oid}/handleAbnormal (not abnormal)", b, json_code=6003)
    else:
        print("  [SKIP] order handleAbnormal — no abnormal orders")

    # ── 6. Edge Cases ───────────────────────────────────────
    print("\n═══ Edge Cases ═══")

    _, b = req("GET", "/admin/buyer/list", auth=False)
    check("No token → 401", b, json_code=401)

    _, b = req("PUT", "/admin/order/1/handleAbnormal", data={"handleResult": "invalid_stuff"})
    check("PUT  /admin/order/1/handleAbnormal (not abnormal → 6003)", b, json_code=6003)

    _, b = req("PUT", "/admin/buyer/99999/ban")
    check("PUT  /admin/buyer/99999/ban (not exist)", b, json_code=1101)

    _, b = req("GET", "/admin/buyer/query", params={"status": ""})
    check("GET  /admin/buyer/query?status= (empty)", b)

    # ── Summary ──────────────────────────────────────────────
    total = PASS + FAIL
    print(f"\n{'='*50}")
    print(f"  PASSED: {PASS}/{total}")
    if FAIL:
        print(f"  FAILED: {FAIL}")
        sys.exit(1)
    else:
        print("  All tests passed!")
        sys.exit(0)


if __name__ == "__main__":
    run()
