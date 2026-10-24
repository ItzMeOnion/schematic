# Schematic preview (Fabric, Minecraft 1.21.8, client-only)

Cosmetic. Only you see it; nothing is sent to the server.

- **Third person (F5 or any third-person view):** your own nametag shows above your character with
  the icon next to it.
- **Tab list:** your entry shows the icon between your skin face and your name, and sits just above
  the media players (below the staff).

## Put your icon in (important)

The mod ships with an original pink star icon. To use a different icon, replace it with your own image:

1. Save your icon image as **logo.png**. Transparent background looks
   best, otherwise you'll see a dark box. Keep it small (about 16x16 up to 64x64 pixels).
2. In your GitHub repo open `src/main/resources/assets/schematic_preview/textures/font/`,
   click **Add file > Upload files**, drop your `logo.png` in (same name, it overwrites the
   star), and commit. The build runs again by itself.

## Setting which media players to sit above

`config/schematic_preview.properties` (made the first time you launch):

    enabled=true
    media_players=Abudoee

`media_players` is a comma separated list of media player names, for example
`Abudoee,SomeoneElse`. You're listed just above the first one of them that's online (in the
server's own order). If none are online, your entry stays wherever the server puts it.

## Build it on GitHub

1. Make a new repo and upload everything in this folder. Create `.github/workflows/build.yml`
   on GitHub itself if your computer hides the `.github` folder (Add file > Create new file).
2. Open **Actions**, pick **build**, run it.
3. Download **schematic-preview-jar**, unzip, and put the jar that does NOT end in `-sources`
   into `.minecraft/mods` with Fabric Loader and Fabric API for 1.21.8.
