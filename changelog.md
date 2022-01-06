# 1.3.0

- Ported to Architectury Plugin / Fabric
- Fixed /reload removing commands
- Forge: Added server display settings so that servers will not inform clients they are missing the mod

# 1.2.5

- Added config option to disable join message on servers.
- Fixed bug where /home would behave oddly between dimensions

# 1.2.4.2

- Fixed mod for Forge 1.14.4-28.1.0+ (breaking changes in 28.1.0)

# 1.2.4.1

- Fixed mod for Forge 1.14.4-28.0.45+ (breaking changes present in .45)

# 1.2.4

- Fixed losing home upon death

# 1.2.3

- Removed /tpd
- Redid command logic for Brigadier (mojang lib)

# 1.2.2

- Redid home data storage system to speed up access time. Also fixed worlds sharing home storage bug.

# 1.2.1

- Fixed file separators to create Linux compatibility.

# 1.2.0

- Added /tpd (dimension ID)

# 1.1.0

- Fixed op requirement on sethome and home.
- Mod not needed client-side to join a server with it installed.
- Fixed inability to teleport with ten blocks of home.
- Cleaned teleport code.
- Mod works on singleplayer correctly.

# 1.0.0

- Initial creation. Has /home and /sethome. Singular homes.