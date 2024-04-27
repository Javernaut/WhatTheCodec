#!/usr/bin/env bash

if [ "$#" != 3 ]; then
    echo "Expecting exactly 3 parameters"
    exit 1
fi

BRAND_COLOR="#3949AB"
RESULT_HEIGHT=1200
RESULT_WIDTH=1920
PADDING=50

SCREENSHOT_HEIGHT=$((RESULT_HEIGHT - 2 * PADDING))

magick -size ${RESULT_WIDTH}x${RESULT_HEIGHT} xc:${BRAND_COLOR} \
          \( "$1" -resize x${SCREENSHOT_HEIGHT} \) -gravity West -geometry +${PADDING} -composite \
          \( "$2" -resize x${SCREENSHOT_HEIGHT} \) -gravity Center -composite \
          \( "$3" -resize x${SCREENSHOT_HEIGHT} \) -gravity East -geometry +${PADDING} -composite \
          screenshots.png
