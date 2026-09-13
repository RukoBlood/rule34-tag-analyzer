# Rule34 Tag Analyzer
Name stands for itself.

Built with Java 21 + JavaFX + Gradle + Gson.

## Setup:
1. Create `please_use_your_own_api_key.json` file in `src/main/resources`
2. Create account on rule34.xxx and In account settings enable api key checkbox. After saving changes, copy an api key from textbox.
3. And in `please_use_your_own_api_key.json` this: 
```json
{
  "api_key": "&api_key=API_KEY_HERE&user_id=UID_HERE"
}
```

## Build
./gradlew build

## Run (In IDE or without compiling it.)
./gradlew run
or
gradlew.bat run

## Run (Compiled JAR)
`java -jar jarname.jar`

You can find jar in build/libs. Note: Run 10mb one, not 300kb one. 300kb is not shadowJar

## Notes
- Don't run without vpn if you are Russian (or if in your country r34 is blocked because child safety o algo). 
- You can't change localization for now through settings. But you can change it in MainView class under lang field. 
- Use only OpenJDK 21 or higher. Do not use Java 1.0-20, especially official/TLauncher bundled Java 8.

## Notes (ChatGPT)
- AI classification is per post: ai_generated OR ai_assisted = one AI post.
- AI percentage is rounded upward to two decimal places.
- Localization lives in src/main/resources/lang/.
- The blocked-IP warning is shown when the API request fails. The app does not inspect or store the user's IP.
