import httpx, random
from datetime import datetime, timezone, timedelta

SUPABASE_URL = "https://qkqrtiitlmhgidnqczbg.supabase.co"
SUPABASE_KEY = ""

headers = {
    "apikey": SUPABASE_KEY,
    "Authorization": f"Bearer {SUPABASE_KEY}",
    "Content-Type": "application/json",
    "Prefer": "return=representation",
}

def post(table, payload):
    r = httpx.post(f"{SUPABASE_URL}/rest/v1/{table}", headers=headers, json=payload)
    if not r.is_success:
        print(f"Error on {table}: {r.status_code} {r.text}")
        r.raise_for_status()
    result = r.json()
    return result if isinstance(result, list) else [result]

def upsert(table, payload, on_conflict):
    r = httpx.post(
        f"{SUPABASE_URL}/rest/v1/{table}",
        headers={**headers, "Prefer": "return=representation,resolution=merge-duplicates"},
        params={"on_conflict": on_conflict},
        json=payload,
    )
    if not r.is_success:
        print(f"Error on {table}: {r.status_code} {r.text}")
        r.raise_for_status()
    result = r.json()
    return result if isinstance(result, list) else [result]

# --- 1. Providers ---
provider_names = ["PharmaCo", "BioLabs", "MediSupply"]
providers = [upsert("providers", {"name": n}, "name")[0] for n in provider_names]
print("Providers:", [p["id"] for p in providers])

# --- 2. Products ---
product_defs = [
    {"provider_id": providers[0]["id"], "name": "Amoxicillin 500mg", "category": "Antibiotic"},
    {"provider_id": providers[0]["id"], "name": "Ibuprofen 200mg",   "category": "Analgesic"},
    {"provider_id": providers[1]["id"], "name": "Reagent X",          "category": "Lab Supply"},
    {"provider_id": providers[2]["id"], "name": "Saline Solution",    "category": "IV Fluid"},
]
products = [upsert("products", p, "provider_id,name")[0] for p in product_defs]
print("Products:", [p["id"] for p in products])

# --- 3. Batches ---
batch_defs = [
    {"product_id": products[0]["id"], "code": "BATCH-A1"},
    {"product_id": products[0]["id"], "code": "BATCH-A2"},
    {"product_id": products[1]["id"], "code": "BATCH-B1"},
    {"product_id": products[2]["id"], "code": "BATCH-C1"},
    {"product_id": products[3]["id"], "code": "BATCH-D1"},
]
batches = [upsert("batches", b, "product_id,code")[0] for b in batch_defs]
batch_ids = [b["id"] for b in batches]
print("Batches:", batch_ids)

# --- 4. Scans ---
SEVERITIES = [
    dict(label="Green",  description="Tag indicates normal state.",   severity="normal",
         r=(20,60),   g=(160,210), b=(60,100), hue=(110.0,150.0), qs=96),
    dict(label="Yellow", description="Tag indicates warning state.",  severity="warning",
         r=(210,240), g=(180,210), b=(5,30),   hue=(40.0, 60.0),  qs=62),
    dict(label="Red",    description="Tag indicates critical state.", severity="critical",
         r=(200,235), g=(20,50),   b=(20,50),  hue=(0.0,  15.0),  qs=24),
]

def randf(lo, hi): return round(random.uniform(lo, hi), 4)

scans = []
for _ in range(150):
    s = random.choices(SEVERITIES, weights=[0.55, 0.30, 0.15])[0]
    days_ago = random.randint(0, 60)
    scanned_at = (
        datetime.now(timezone.utc) - timedelta(days=days_ago, hours=random.randint(0, 23))
    ).isoformat()

    scans.append({
        "batch_id":    random.choice(batch_ids),
        "source":      random.choice(["live_camera", "gallery_image"]),
        "red":         random.randint(*s["r"]),
        "green":       random.randint(*s["g"]),
        "blue":        random.randint(*s["b"]),
        "hue":         randf(*s["hue"]),
        "saturation":  randf(0.70, 0.97),
        "value":       randf(0.65, 0.97),
        "confidence":  randf(0.55, 0.92),
        "interpretation_label":       s["label"],
        "interpretation_description": s["description"],
        "interpretation_severity":    s["severity"],
        "quality_score": s["qs"],
        "roi_x": None, "roi_y": None, "roi_width": None, "roi_height": None,
        "note":       None,
        "image_path": None,
        "scanned_at": scanned_at,
    })

# Insert in chunks of 50
CHUNK = 50
for i in range(0, len(scans), CHUNK):
    post("scans", scans[i:i+CHUNK])
    print(f"Inserted scans {i+1}–{min(i+CHUNK, len(scans))}")

print("Done.")
