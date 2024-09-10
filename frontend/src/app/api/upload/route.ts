import { NextResponse } from "next/server";

export async function POST(req: Request) {
  const formData = await req.formData();
  const file = formData.get("file") as File;

  if (!file) {
    return NextResponse.json({ error: "No file uploaded" }, { status: 400 });
  }

  const apiKey = process.env.API_KEY;
  const backendUrl = process.env.BACKEND_URL;

  const url = `${backendUrl}/api/upload`;

  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "x-api-key": apiKey as string,
      },
      body: formData,
      cache: "no-store",
    });

    if (!response.ok) {
      throw new Error("Upload failed");
    }

    const data = await response.json();
    return NextResponse.json(data);
  } catch (error) {
    console.error("Upload error:", error);
    return NextResponse.json({ error: "Upload failed" }, { status: 500 });
  }
}
