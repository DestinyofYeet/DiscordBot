import pathlib

from captcha.image import ImageCaptcha
import string
import random
import sys

import json

image = ImageCaptcha(width=280, height=90)

chars = string.ascii_letters

text = ""

for i in range(10):
    text += random.choice(chars)

data = image.generate(text)

output = f"{sys.argv[1]}_captcha.png"

image.write(text, output)

path = pathlib.Path(output)

print(json.dumps({"key": text, "path": str(path.absolute())}))