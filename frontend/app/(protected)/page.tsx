import { AuthFlowTest } from "@/features/auth/ui/AuthFlowTest";
import { type Metadata } from "next/types";

export async function generateMetadata(): Promise<Metadata> {
  return { title: "Auth Test" };
}

export default function AuthTestPage() {
  return <AuthFlowTest />;
}
