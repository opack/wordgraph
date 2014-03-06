set LIBGDX_PATH=C:/Progz/dev/libgdx-nightly-latest_0.9.6
set INPUT_DIR=images
set OUTPUT_DIR=.
set PACK_FILENAME=default-puzzle.atlas
java -cp %LIBGDX_PATH%/gdx.jar;%LIBGDX_PATH%/extensions/gdx-tools.jar com.badlogic.gdx.tools.imagepacker.TexturePacker2 %INPUT_DIR% %OUTPUT_DIR% %PACK_FILENAME%
pause