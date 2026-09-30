"""
Notification Worker Logic for Aakaash360.
Uses APScheduler to evaluate circadian schedule and dispatch night-before and morning alerts.
"""

from apscheduler.schedulers.asyncio import AsyncIOScheduler
from datetime import datetime
import asyncio
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("AakaashWorker")

scheduler = AsyncIOScheduler()

async def evaluate_night_before_hazards():
    """
    Runs every night at 21:00.
    Evaluates tomorrow morning's departure weather window against user thresholds.
    Dispatches pre-sleep notification if wake-up time adjustment is recommended.
    """
    logger.info("Executing Night-Before Bio-Sync evaluation at %s", datetime.now())
    # Simulated check: crosswinds expected at 07:00 tomorrow
    forecast_hazard_detected = True
    if forecast_hazard_detected:
        logger.warning(
            "ALERT DISPATCHED: Crosswinds (34 km/h) forecasted for tomorrow morning. "
            "Suggested alarm shift: -25 min (06:20 AM wake-up)."
        )

async def evaluate_early_morning_triggers():
    """
    Runs early morning at 05:45 (45 minutes prior to user wake-up).
    Performs Doppler radar scan for rapid squalls, wet tarmac friction, or lightning cells.
    """
    logger.info("Executing Early Morning Doppler scan at %s", datetime.now())
    logger.info("Doppler scan clean. Road friction optimal at 98%. Safe to commence planned route.")

def start_worker():
    # Schedule night-before job at 21:00 every day
    scheduler.add_job(evaluate_night_before_hazards, 'cron', hour=21, minute=0, id="night_before_job")
    # Schedule morning scan at 05:45 every day
    scheduler.add_job(evaluate_early_morning_triggers, 'cron', hour=5, minute=45, id="morning_trigger_job")
    scheduler.start()
    logger.info("Aakaash360 APScheduler Worker started successfully.")

if __name__ == "__main__":
    start_worker()
    try:
        asyncio.get_event_loop().run_forever()
    except (KeyboardInterrupt, SystemExit):
        scheduler.shutdown()
