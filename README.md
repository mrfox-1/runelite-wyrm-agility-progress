# Wyrm Agility AFK Progress

RuneLite progress timers for the lower and advanced Colossal Wyrm agility
routes. The timings include travel and automatic movement across each obstacle,
making it easier to look away during the course and return when the next
obstacle is ready.

<img width="640" height="360" alt="wyrm-agility-demo-10s-github" src="https://github.com/user-attachments/assets/09ff9306-16d0-4bc1-8e0d-b57a5a54c3f2" />

<img width="237" height="195" alt="image" src="https://github.com/user-attachments/assets/2f49fb69-2d17-4d6a-8cc5-962200b28b85" />

## Features

- A movable progress bar with a single remaining-time display.
- Fixed timings measured from complete laps of both Wyrm routes.
- Optional local-only overhead countdown above the player.
- Optional completion sound with a configurable RuneLite sound effect ID.
- Protection against premature distant clicks replacing the active timer.

The plugin does not automate input or interact with obstacles for the player.

## Development

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

Java 11 or newer is required to build the plugin.

## License

BSD 2-Clause License. See `LICENSE`.
