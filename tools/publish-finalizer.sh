#!/bin/bash
set -euo pipefail # Exit immediately if a command exits with a non-zero status

USERNAME="$1"
PASSWORD="$2"
NAMESPACE="$3"
API_URL="https://ossrh-staging-api.central.sonatype.com/manual/search/repositories?ip=any&profile_id=$NAMESPACE"
FINALIZE_URL="https://ossrh-staging-api.central.sonatype.com/manual/upload/repository"

echo "1. Searching for OPEN staged repositories for namespace: $NAMESPACE"

# 1. GET request with 'ip=any' flag to find ALL repositories
# -s: silent output, -X GET: method
RESPONSE=$(curl -s -X GET -u "$USERNAME:$PASSWORD" "$API_URL")

# 2. Extract ALL staged repository IDs that are currently "OPEN"
# .data[] filters the main list, | select(.state == "open") keeps only open ones,
# and .key extracts the unique repository key.
OPEN_REPOS=$(echo "$RESPONSE" | jq -r '.repositories[] | select(.state == "open") | .key')

if [ -z "$OPEN_REPOS" ]; then
    echo "No open repositories found; deployment is clean or already finalized."
    echo "⚠️ Check the Maven Central Portal to  confirm if the deployment is successful."
    exit 0
fi

echo "2. Found OPEN repositories: $OPEN_REPOS"
echo "3. Finalizing (POST) each open repository..."

# 3. Loop through each OPEN repository ID and send the finalization POST request
for REPO_KEY in $OPEN_REPOS; do
    echo "   -> Processing Repository: $REPO_KEY"

    # POST request to finalize the specific repository by its unique key
    FINAL_RESPONSE=$(curl -i -X POST -u "$USERNAME:$PASSWORD" "$FINALIZE_URL/$REPO_KEY")
    # Check the HTTP status code (looking for HTTP/2 200 or 202)
    STATUS=$(echo "$FINAL_RESPONSE" | grep "HTTP/2" | awk '{print $2}')

    if [[ "$STATUS" == "200" || "$STATUS" == "202" ]]; then
        echo "   ✅ SUCCESS: Repository $REPO_KEY finalized."
    else
        echo "   ❌ FAILURE: Repository $REPO_KEY finalization FAILED with status $STATUS."
        # If any finalization fails, we stop the script and signal failure.
        exit 1
    fi
done

echo "4. All open repositories successfully finalized and ready for manual release! ☑️"