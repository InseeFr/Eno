# Multimode feature

## Concept

The idea is that a questionnaire can be collected:

- on the web (self-administered by the respondent)
- by telephone or face to face (by an interviewer)

We want Lunatic orchestrators to send an event based on some condition defined in the questionnaire.

This event is for example "IS_MOVED", meaning the respondent adress' has changed.

This event is designed to be received by synchronization tools that will pass the questionnaire to the appropriate target.

## Pogues

In the Pogues questionnaire object:

```json
{
    "multimode": {
        "questionnaire": {
            "rules": [
                {
                    "name": "IS_MOVED",
                    "type": "VTL",
                    "value": "nvl(IS_ADDRESS_CORRECT, true)"
                }
            ]
        },
        "leaf": {
            "rules": [
                {
                    "name": "IS_MOVED",
                    "type": "VTL",
                    "value": "nvl(HAS_MOVED, false)"
                }
            ]
        }
    }
}
```

Note: _leaf_ rules are the rules for the roundabout level.

## DDI

:x: Not described in DDI.

## Lunatic

In the Lunatic questionnaire object:

```json
{
   "multimode": {
        "questionnaire": {
            "rules": {
                "IS_MOVED": {
                    "type": "VTL",
                    "value": "nvl(HABITEZ_VOUS_ICI, true)"
                }
            }
        },
        "leaf": {
            "source": "id-roundabout-in-questionnaire",
            "rules": {
                "IS_MOVED": {
                    "type": "VTL",
                    "value": "nvl(PRENOM_HABITE_PLUS_LA, false)"
                }
            }
        }
    }
}
```
