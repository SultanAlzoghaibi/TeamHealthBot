# save as locustfile.py
from locust import HttpUser, task, constant
import random
import urllib.parse as up

# ---- test options ----



class SlackBotUser(HttpUser):
    wait_time = constant(1)        # ~sleep(1) between requests
    host = "http://acc8596f6234747a89ce9b05617ceb88-274964173.us-east-1.elb.amazonaws.com"

    @task
    def post_slack_command(self):
        payload = {
            "token": "TEST_SLACK_TOKEN",
            "team_id": "T0001",
            "team_domain": "example",
            "channel_id": "C2147483705",
            "channel_name": "test",
            "user_id": f"U{random.randint(0, 9_999_999)}",
            "user_name": "john",
            "command": "/myscores",
            "text": "(random.randint(1, 100))",
            "response_url": "https://hooks.slack.com/commands/1234/5678",
            "trigger_id": "13345224609.738474920.8088930838d88f008e0",
        }

        encoded = up.urlencode(payload)
        headers = {"Content-Type": "application/x-www-form-urlencoded"}
        self.client.post("/api/slack/commands", data=encoded, headers=headers)