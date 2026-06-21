#!/usr/bin/env python3
"""Generate mental health app icon for '静了么'."""
import struct
import zlib
import math

def create_png(width, height, color_fn):
    raw = b''
    for y in range(height):
        raw += b'\x00'
        for x in range(width):
            r, g, b, a = color_fn(x, y, width, height)
            raw += bytes([r, g, b, a])
    def chunk(ctype, data):
        c = ctype + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xffffffff)
    png = b'\x89PNG\r\n\x1a\n'
    png += chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0))
    png += chunk(b'IDAT', zlib.compress(raw))
    png += chunk(b'IEND', b'')
    return png

def mind_icon(x, y, w, h):
    cx, cy = w // 2, h // 2
    border = 4
    corner_r = border + 2
    corners = [(border, border), (w-border, border), (border, h-border), (w-border, h-border)]
    for corner in corners:
        if math.sqrt((x - corner[0])**2 + (y - corner[1])**2) > corner_r:
            if (x < border and y < border) or (x >= w-border and y < border) or (x < border and y >= h-border) or (x >= w-border and y >= h-border):
                return (0, 0, 0, 0)
    dist = math.sqrt((x - cx)**2 + (y - cy)**2)
    max_dist = math.sqrt(cx**2 + cy**2)
    t = dist / max_dist
    bg_r = int(15 - t * 5)
    bg_g = int(30 - t * 10)
    bg_b = int(45 - t * 15)

    # Meditation figure (simplified person sitting)
    fig_cx = cx
    fig_cy = int(h * 0.45)
    # Head
    head_r = int(w * 0.1)
    head_dist = math.sqrt((x - fig_cx)**2 + (y - (fig_cy - int(h * 0.12)))**2)
    if head_dist < head_r:
        r = min(255, int(180 + (1 - head_dist / head_r) * 60))
        g = min(255, int(160 + (1 - head_dist / head_r) * 60))
        b = min(255, int(140 + (1 - head_dist / head_r) * 60))
        return (r, g, b, 255)
    # Body (crossed legs)
    body_top = fig_cy - int(h * 0.04)
    body_bottom = fig_cy + int(h * 0.12)
    body_left = int(w * 0.35)
    body_right = int(w * 0.65)
    if body_left <= x <= body_right and body_top <= y <= body_bottom:
        r, g, b = 160, 140, 180
        if x - body_left < 3 or body_right - x < 3:
            r, g, b = int(r * 0.6), int(g * 0.6), int(b * 0.6)
        return (r, g, b, 255)

    # Aura/halo (concentric circles)
    for i, aura_r in enumerate([int(w * 0.28), int(w * 0.32), int(w * 0.36)]):
        aura_dist = math.sqrt((x - cx)**2 + (y - (fig_cy - int(h * 0.04)))**2)
        if abs(aura_dist - aura_r) < 2:
            alpha = int(80 - i * 20)
            return (100, 180, 160, alpha)

    # Small lotus petals around
    for angle in [0, 60, 120, 180, 240, 300]:
        rad = math.radians(angle)
        px = int(cx + math.cos(rad) * w * 0.4)
        py = int(cy + math.sin(rad) * h * 0.4)
        petal_dist = math.sqrt((x - px)**2 + (y - py)**2)
        if petal_dist < int(w * 0.06):
            return (120, 200, 170, 180)

    # Stars
    stars = [(0.15, 0.2), (0.85, 0.2), (0.5, 0.12), (0.2, 0.8), (0.8, 0.8)]
    for sx, sy in stars:
        star_x = int(w * sx); star_y = int(h * sy)
        star_r = int(w * 0.02)
        if math.sqrt((x - star_x)**2 + (y - star_y)**2) < star_r:
            return (200, 220, 255, 200)

    return (bg_r, bg_g, bg_b, 255)

sizes = {'mdpi': 48, 'hdpi': 72, 'xhdpi': 96, 'xxhdpi': 144, 'xxxhdpi': 192}
output_dir = '/root/mind-tracker/android/app/src/main/res'
import os

for density, size in sizes.items():
    png_data = create_png(size, size, mind_icon)
    path = os.path.join(output_dir, f'mipmap-{density}', 'ic_launcher.png')
    with open(path, 'wb') as f:
        f.write(png_data)
    print(f'Created {path} ({size}x{size}, {len(png_data)} bytes)')
    path_round = os.path.join(output_dir, f'mipmap-{density}', 'ic_launcher_round.png')
    with open(path_round, 'wb') as f:
        f.write(png_data)

print('\nAll mind icons generated!')
