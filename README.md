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
./gradlew build, but you can't run jar directly

## Run
./gradlew run

On Windows:
gradlew.bat run

## Notes (ChatGPT)
- AI classification is per post: ai_generated OR ai_assisted = one AI post.
- AI percentage is rounded upward to two decimal places.
- Localization lives in src/main/resources/lang/.
- The blocked-IP warning is shown when the API request fails. The app does not inspect or store the user's IP.
- Add Inter font files to src/main/resources/fonts/ for bundled typography.
